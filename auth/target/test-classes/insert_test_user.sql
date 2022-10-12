INSERT
INTO public.users(name)
VALUES ('test_username');

INSERT
INTO public.users_roles(user_id, roles_id)
VALUES((SELECT id
        FROM public.users
        WHERE name = 'test_username'),
       2);