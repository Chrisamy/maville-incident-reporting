
if (!window.eventSource) {
    window.eventSource = new EventSource("/events");
    window.eventSource.onmessage = (event) => {
        alert(event.data);
        };
}