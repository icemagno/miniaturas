var Toast = Swal.mixin({
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 5000
});

function alertToast(title, text, icon = 'success') {
    Toast.fire({
        title: title,
        text: text,
        icon: icon
    });
}

$(document).ready(function() {


});

