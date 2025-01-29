INSERT INTO payments (id, status, booking_id, url, session_id, amount)
VALUES
    (1, 'PENDING', 1, 'https://checkout.stripe.com/c/pay/cs_test_a1QpOwJuVdsnxVDllwnHk82',
     'cs_test_a1ADMDWWeKAzWJ6RwzeyjmDSjdy0rHsvOJTmUZfZmISHIh6PsawrGLKe0i', 1000.00),
    (2, 'PENDING', 2, 'https://checkout.stripe.com/c/pay/cs_test_a1QpOwJuVdsnxVDllwnHk83',
     'cs_test_a1ADMDWWeKAzWJ6RwzeyjmDSjdy0rHsvOJTmUZfZmISHIh6PsawrGLKe0y', 1000.00),
    (3, 'PENDING', 3, 'https://checkout.stripe.com/c/pay/cs_test_a1QpOwJuVdsnxVDllwnHk84',
     'cs_test_a1ADMDWWeKAzWJ6RwzeyjmDSjdy0rHsvOJTmUZfZmISHIh6PsawrGLKe0o', 1000.00);
