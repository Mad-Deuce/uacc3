DELETE
FROM public.users_roles
WHERE user_id = (SELECT id
                 FROM public.users
                 WHERE name = 'test_username');
DELETE
FROM public.users
WHERE name = 'test_username';