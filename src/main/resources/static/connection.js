let localStream = null;
let startButton;
let stopButton;
let csrfToken;
let csrfHeader;
let stompClient;
let peerConnections = {};
let framerate = 30;
let mediaOptions = {video: true};
let isAudio = false;

document.addEventListener("DOMContentLoaded", () => {
    startButton = document.querySelector(".start-sharing");
    stopButton = document.querySelector(".stop-sharing");
    csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    startButton.addEventListener("click", startSharing);
    stopButton.addEventListener("click", stopSharing);
    document.querySelector("#resolution-picker").addEventListener("change", resolutionChange);
    document.querySelector("#framerate-picker").addEventListener("change", framerateChange);
    document.querySelector("#isAudio").addEventListener("change", (e) => isAudio = e.target.checked);
    connectToSignalingServer();
});

function connectToSignalingServer() {
    const domain = window.location.hostname;
    const port = window.location.port ? `:${window.location.port}` : '';

    stompClient = new StompJs.Client({
        brokerURL: `wss://${domain}${port}/signaling`,
        debug: (str) => {
            console.log(str);
        },
        connectHeaders: {
            [csrfHeader]: csrfToken
        }
    });

    stompClient.onConnect = (frame) => {
        stompClient.subscribe(`/user/queue/current-users`, (message) => {
            const data = JSON.parse(message.body);
            if (data.type === "join") {
                handleJoin(data);
            } else if (data.type === "leave") {
                handlePeerDisconnection(data);
            }
        });

        stompClient.subscribe(`/user/queue/offer`, (msg) => {
            handleOffer(JSON.parse(msg.body));
        });

        stompClient.subscribe(`/user/queue/answer`, (msg) => {
            handleAnswer(JSON.parse(msg.body));
        });

        stompClient.subscribe(`/user/queue/ice`, (msg) => {
            handleIce(JSON.parse(msg.body));
        });
    };

    stompClient.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    };

    stompClient.activate();
}

async function startSharing() {
    mediaOptions["audio"] = isAudio;
    try {
        localStream = await navigator.mediaDevices.getDisplayMedia(mediaOptions);
        stopButton.disabled = false;
        startButton.disabled = true;
        createVideoElement(localStream, 'local');
        for (let [id, pc] of Object.entries(peerConnections)) {
            addStreamToPeerConnection(pc);
            createOffer(pc, id);
        }
        localStream.getTracks().forEach(track => {
           track.onended = () => {
               stopSharing();
           };
        });
    } catch (error) {
        console.error("There is no access to the screen.", error);
    }
}

function stopSharing() {
    if (localStream) {
        for (let [id, pc] of Object.entries(peerConnections)) {
            removeStreamFromPeerConnection(pc);
        }
        localStream.getTracks().forEach(track => track.stop());
        removeVideoElement('local');
        stopButton.disabled = true;
        startButton.disabled = false;
        localStream = null;
    }
}

function createVideoElement(stream, id) {
    const videoWrapper = document.querySelector('.video-wrapper');
    const video = document.createElement('video');
    video.srcObject = stream;
    video.autoplay = true;
    video.controls = true;
    video.id = id;
    videoWrapper.appendChild(video);
}

function removeVideoElement(id) {
    const video = document.getElementById(id);
    if (video) {
        video.srcObject = null;
        video.remove();
    }
}

function getPeerConnection(sessionId) {
    if (peerConnections[sessionId]) {
        return peerConnections[sessionId];
    }
    const peerConnection = new RTCPeerConnection({
        iceServers: [
            {urls: 'stun:stun.l.google.com:19302'}
        ]
    });
    peerConnections[sessionId] = peerConnection;

    peerConnection.onicecandidate = event => {
        if (event.candidate) {
            sendData({
                targetId: sessionId,
                type: "ice",
                data: event.candidate
            });
        }
    };

    peerConnection.ontrack = event => {
        const stream = event.streams[0];
        createVideoElement(stream, sessionId);
        stream.onremovetrack = (track) => {
            removeVideoElement(sessionId);
        };
    };

    peerConnection.onnegotiationneeded = (ev) => {
        createOffer(peerConnection, sessionId);
    };

    return peerConnection;
}

function handleOffer(offer) {
    let peerId = offer.senderId;
    let conn = getPeerConnection(peerId);
    conn.setRemoteDescription(new RTCSessionDescription(offer.data)).then(() => {
        conn.createAnswer().then((ans) => {
            conn.setLocalDescription(ans).then(() => {
                sendData({
                    type: "answer",
                    targetId: peerId,
                    data: JSON.stringify(ans)
                });
            }).catch(e => console.error("setLocalDescription error: ", e));
        }).catch(e => console.error("createAnswer error: ", e));
    }).catch(e => console.error("setRemoteDescription error: ", e));
}

function handleAnswer(answer) {
    let peerId = answer.senderId;
    let conn = getPeerConnection(peerId);
    conn.setRemoteDescription(new RTCSessionDescription(JSON.parse(answer.data))).catch(e => console.error("handleAnswer error : ", e));
}

function handleIce(ice) {
    if (ice.data) {
        let conn = getPeerConnection(ice.senderId);
        conn.addIceCandidate(new RTCIceCandidate(ice.data)).catch(e => console.log("handle ice error: ", e));
    }
}

function handlePeerDisconnection(data) {
    let peerId = data.senderId;
    removeVideoElement(peerId);
    if (peerConnections[peerId]) {
        peerConnections[peerId].close();
        delete peerConnections[peerId];
    }
}

function handleJoin(data) {
    let peerId = data.senderId;
    let conn = getPeerConnection(peerId);
    createOffer(conn, peerId);
    if (localStream) {
        addStreamToPeerConnection(conn);
    }
}

function addStreamToPeerConnection(pc) {
    localStream.getTracks().forEach(track => {
        pc.addTrack(track, localStream);
    });
}

function removeStreamFromPeerConnection(pc) {
    pc.getSenders().forEach(sender => {
        pc.removeTrack(sender);
    });
}

function sendData(data) {
    stompClient.publish({
        destination: '/app/dataExchange',
        body: JSON.stringify(data)
    });
}

function createOffer(conn, peerId){
    conn.createOffer().then((sdp) => {
        conn.setLocalDescription(sdp).then(() => {
            sendData({
                type: "offer",
                targetId: peerId,
                data: sdp
            });
        }).catch(e => console.error("SetLocalDescription error: ", e));
    }).catch(e => console.error("CreateOffer error: ", e));
}

function resolutionChange(e){
    let val = parseInt(e.target.value);
    if(val === 0){
        mediaOptions = {video: true};
    }else if(val === 480){
        mediaOptions = {
            video: {
                height: {ideal: val},
                frameRate: {ideal: framerate}
            }
        }
    }else if(val === 720){
        mediaOptions = {
            video: {
                height: {ideal: val},
                frameRate: {ideal: framerate}
            }
        }
    }else if(val === 1080){
        mediaOptions = {
            video: {
                height: {ideal: val},
                frameRate: {ideal: framerate}
            }
        }
    }else if(val === 1440){
        mediaOptions = {
            video: {
                height: {ideal: val},
                frameRate: {ideal: framerate}
            }
        }
    }
}

function framerateChange(e){
    let val = parseInt(e.target.value);
    if(val === 15){
        framerate = 15;
    }else if(val === 30){
        framerate = 30;
    }else if(val === 60){
        framerate = 60;
    }
}
