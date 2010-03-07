$(document).ready(function() {
$('#s-from').datePicker({startDate:'01/01/1996'});
$('#s-to').datePicker({startDate:'01/01/1996'});
$("table").tablesorter( {
	widgets: ["zebra"],
	sortList:[[0,0]]
});
});
