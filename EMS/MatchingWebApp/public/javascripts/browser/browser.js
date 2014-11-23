
window.selectedIds = {};

// TODO: Where does this belong to?
function handleStatus(data) {
	var status = data["status"];
	if (status != "success") {
		alert("Failure, sorry next time we will show a nice modal with the alert");
	}
}