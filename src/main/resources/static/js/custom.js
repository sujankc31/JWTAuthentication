// This script is for homepage
// Form validation script
document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('form');

    // Form elements
    const nameInput = document.getElementById('name');
    const emailInput = document.getElementById('email');
    const subjectInput = document.getElementById('subject');
    const messageInput = document.getElementById('message');

    // Validation patterns
    const emailPattern = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;

    // Email examples for reference
    const validEmailExamples = [
        'user@example.com',
        'first.last@example.co.uk',
        'user.name+tag@example.org'
    ];

    // Error messages container
    const createErrorMessage = (input, message) => {
        const existingError = input.parentElement.querySelector('.error-message');
        if (existingError) existingError.remove();

        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message text-danger mt-1';
        errorDiv.innerHTML = message; // Changed from textContent to innerHTML to support HTML in error messages
        input.parentElement.appendChild(errorDiv);
        input.classList.add('is-invalid');
    };

    // Remove error message
    const removeErrorMessage = (input) => {
        const existingError = input.parentElement.querySelector('.error-message');
        if (existingError) existingError.remove();
        input.classList.remove('is-invalid');
        input.classList.add('is-valid');
    };

    // Validate name
    const validateName = () => {
        if (nameInput.value.trim() === '') {
            createErrorMessage(nameInput, 'Please enter your name');
            return false;
        } else if (nameInput.value.trim().length < 2) {
            createErrorMessage(nameInput, 'Name must be at least 2 characters');
            return false;
        } else {
            removeErrorMessage(nameInput);
            return true;
        }
    };

    // Validate email
    const validateEmail = () => {
        const email = emailInput.value.trim();

        if (email === '') {
            createErrorMessage(emailInput, 'Please enter your email address');
            return false;
        }

        // Check against pattern
        else if (!emailPattern.test(email)) {
            // Create detailed error message with examples
            let errorMsg = 'Please enter a valid email address';

            // Add specific validation hints based on common errors
            if (!email.includes('@')) {
                errorMsg += ' (missing @ symbol)';
            } else if (!email.includes('.')) {
                errorMsg += ' (missing domain extension)';
            } else if (email.indexOf('@') === email.length - 1) {
                errorMsg += ' (missing domain after @)';
            } else if (email.startsWith('@') || email.startsWith('.')) {
                errorMsg += ' (cannot start with @ or .)';
            }

            // Add examples
            errorMsg += '<br><small>Examples: ' + validEmailExamples.join(', ') + '</small>';

            createErrorMessage(emailInput, errorMsg);
            return false;
        }

        else {
            removeErrorMessage(emailInput);
            return true;
        }
    };

    // Validate subject
    const validateSubject = () => {
        if (subjectInput.value.trim() === '') {
            createErrorMessage(subjectInput, 'Please enter a subject');
            return false;
        } else if (subjectInput.value.trim().length < 5) {
            createErrorMessage(subjectInput, 'Subject must be at least 5 characters');
            return false;
        } else {
            removeErrorMessage(subjectInput);
            return true;
        }
    };

    // Validate message
    const validateMessage = () => {
        if (messageInput.value.trim() === '') {
            createErrorMessage(messageInput, 'Please enter your message');
            return false;
        } else if (messageInput.value.trim().length < 10) {
            createErrorMessage(messageInput, 'Message must be at least 10 characters');
            return false;
        } else {
            removeErrorMessage(messageInput);
            return true;
        }
    };

    // Real-time validation
    nameInput.addEventListener('input', validateName);
    emailInput.addEventListener('input', validateEmail);
    subjectInput.addEventListener('input', validateSubject);
    messageInput.addEventListener('input', validateMessage);

    // Form submission
    form.addEventListener('submit', (e) => {
        e.preventDefault();

        // Validate all fields
        const isNameValid = validateName();
        const isEmailValid = validateEmail();
        const isSubjectValid = validateSubject();
        const isMessageValid = validateMessage();

        // If all validations pass
        if (isNameValid && isEmailValid && isSubjectValid && isMessageValid) {
            // Submit the form - add your AJAX submission or form processing here
            console.log('Form validated successfully!');

            // You can uncomment this to actually submit the form
            form.submit();

            // For demonstration, show success message
            const successMessage = document.createElement('div');
            successMessage.className = 'alert alert-success mt-3';
            successMessage.textContent = 'Your message has been sent successfully!';
            form.appendChild(successMessage);

            // Remove success message after 3 seconds
            setTimeout(() => {
                successMessage.remove();
                form.reset();
                document.querySelectorAll('.is-valid').forEach(el => el.classList.remove('is-valid'));
            }, 3000);
        }
    });

    // js for number animation for counter section in homepage
    const counters = document.querySelectorAll('.counter');
    counters.forEach(counter => {
        const target = +counter.getAttribute('data-target');
        const increment = target / 200;
        let current = 0;
        const updateCounter = () => {
            if (current < target) {
                current += increment;
                counter.innerText = Math.ceil(current);
                setTimeout(updateCounter, 10);
            } else {
                counter.innerText = target;
            }
        }
        updateCounter();
    });
});
