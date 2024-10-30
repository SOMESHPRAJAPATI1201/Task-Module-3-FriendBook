document.addEventListener('DOMContentLoaded', () => {
            const toastLiveExample = document.getElementById('liveToast');

            if (toastLiveExample) {
                const toastBootstrap = bootstrap.Toast.getOrCreateInstance(toastLiveExample);
                toastBootstrap.show();
            }
        });

                function validateImageUpload() {
                    const fileInput = document.getElementById('profileImage');
                    const file = fileInput.files[0];
                    const allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];
                    const maxSize = 5 * 1024 * 1024;  // 5 MB

                    if (!allowedTypes.includes(file.type)) {
                        alert('Invalid file type. Only JPEG, PNG, and GIF are allowed.');
                        return false;
                    }

                    if (file.size > maxSize) {
                        alert('File size exceeds 5 MB.');
                        return false;
                    }

                    return true;
                }


                    (function () {
                        'use strict';
                        const forms = document.querySelectorAll('.needs-validation');
                        Array.prototype.slice.call(forms).forEach(function (form) {
                            form.addEventListener('submit', function (event) {
                                if (!form.checkValidity()) {
                                    event.preventDefault();
                                    event.stopPropagation();
                                }
                                form.classList.add('was-validated');
                            }, false);
                        });
                    })();

//RegisterPage

document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('registrationForm');

    form.addEventListener('submit', function (event) {
        event.preventDefault();  // Prevent default form submission

        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;  // Stop if form validation fails
        }

        const formData = new FormData();

        const user = {
            firstname: document.getElementById('firstname').value,
            lastname: document.getElementById('lastname').value,
            username: document.getElementById('username').value,
            address: document.getElementById('address').value,
            address2: document.getElementById('address2').value,
            state: document.getElementById('state').value,
            city: document.getElementById('city').value,
            zip: document.getElementById('zip').value,
            password: document.getElementById('password').value,
        };

        formData.append('user', new Blob([JSON.stringify(user)], { type: 'application/json' }));
        const profileImage = document.getElementById('profileImage').files[0];
        formData.append('profileImage', profileImage);

        fetch('/regForm', {
            method: 'POST',
            body: formData,
            headers: {
                'Accept': 'text/html',  // Request HTML response if desired
            },
        })
            .then(response => {
                if (response.ok) {
                    const contentType = response.headers.get('content-type');
                    if (contentType && contentType.includes('text/html')) {
                        // If HTML, redirect to that page
                        return response.text().then(html => {
                            document.open();
                            document.write(html);
                            document.close();
                        });
                    } else {
                        return response.json().then(data => {
                            console.log('User registered:', data);
                            alert('Registration successful!');
                        });
                    }
                } else {
                    throw new Error('Issue with given details');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred during registration.', error);
            });
    });
});

//Login Page






