-module(main).
-export([server/1, room/1]).

server(Port) ->
	Channel = spawn(fun()-> room([]) end),
	{ok, LSock} = gen_tcp:listen(Port, [binary, {packet, line}, {reuseaddr, true}]),
	acceptor(LSock, Channel).

acceptor(LSock, Channel) ->
	{ok, Sock} = gen_tcp:accept(LSock),
  	spawn(fun() -> acceptor(LSock, Channel) end),
  	Channel ! {new_user, self()},
  	client(Sock, Channel).

room(Pids) ->
  	receive
	    {new_user, Pid} ->
	      io:format("new user~n", []),
	      room([Pid | Pids]);
	   	{leave, Pid} -> 
	   		io:format("user left~n", []),
	    	room(Pids -- [Pid])
  	end.

client(Sock, Channel) ->
	receive
		{tcp, _, Data} ->
			X = protoAuthOrderErlang:decode_msg(Data, 'Auth'),
			io:format(X, []),
			client(Sock, Channel);
		{tcp_closed, Sock} ->
			Channel ! {leave, self()};
   		{tcp_error, Sock, _} ->
   			Channel ! {leave, self()}
	end.


