document.addEventListener("htmx:configRequest", (event) => {
    let token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    let header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    event.detail.headers[header] = token;
});