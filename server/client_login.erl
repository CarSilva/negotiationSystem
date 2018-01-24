-module(client_login).
-export([create_account/2, close_account/2, login/2, logout/1, start/0]).

start() -> register(?MODULE, spawn(fun() -> loop(#{}) end)).


create_account(U, P) ->
    ?MODULE ! {create_account, U, P, self()},
    receive
        {?MODULE, Res} -> Res
    end.

close_account(U, P) ->
    ?MODULE ! {close_account, U, P, self()},
    receive
        {?MODULE, Res} -> Res
    end.

login(U, P) ->
    ?MODULE ! {login, U, P, self()},
    receive
        {?MODULE, Res} -> Res
    end.

logout(U) ->
    ?MODULE ! {logout, U, self()},
    receive
        {?MODULE, Res} -> Res
    end.

loop(M) ->
    receive
        {create_account, U, P, From} ->
            case maps:find(U, M) of
                error ->
                    From ! {?MODULE, ok},
                    loop(maps:put(U, {P, true}, M));
                    _ ->
                        From ! {?MODULE, user_exists},
                        loop(M)
            end;
        {close_account, U, P, From} ->
            case maps:find(U, M) of
                {ok, {P, _}} ->
                    From ! {?MODULE, ok},
                    loop(maps:remove(U, M));
                _ ->
                    From ! {?MODULE, invalid},
                    loop(M)
            end;
        {login, U, P, From} ->
            case maps:find(U, M) of
                {ok, {P, _}} ->
                    From ! {?MODULE, ok},
                    loop(maps:update(U, {P, true}, M));
                _ ->
                    From ! {?MODULE, invalid},
                    loop(M)
            end;
        {logout, U, From} ->
            case maps:find(U, M) of
                {ok, {P, _}} ->
                    From ! {?MODULE, ok},
                    loop(maps:update(U, {P, false}, M));
                _ ->
                    From ! {?MODULE, ok},
                    loop(M)
            end
    end.
