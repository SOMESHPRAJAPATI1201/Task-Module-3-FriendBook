document.addEventListener('DOMContentLoaded', () => {
            const toastLiveExample = document.getElementById('liveToast');

            if (toastLiveExample) {
                const toastBootstrap = bootstrap.Toast.getOrCreateInstance(toastLiveExample);
                toastBootstrap.show();
            }
        });

                // Client-side validation
                function validateImageUpload() {
                    const fileInput = document.getElementById('profileImage');
                    const file = fileInput.files[0];
                    const allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];
                    const maxSize = 5 * 1024 * 1024;  // 5 MB

                    // Check file type
                    if (!allowedTypes.includes(file.type)) {
                        alert('Invalid file type. Only JPEG, PNG, and GIF are allowed.');
                        return false;
                    }

                    // Check file size
                    if (file.size > maxSize) {
                        alert('File size exceeds 5 MB.');
                        return false;
                    }

                    return true;
                }


                // Optional: Add Bootstrap validation styles
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






