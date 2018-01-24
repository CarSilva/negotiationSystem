-module(main).
-export([server/1]).
-include("protobuf/protoAuth.hrl").
-include("protobuf/protoReqRecv.hrl").

server(Port) ->
	client_login:start(),
	{ok, LSock} = gen_tcp:listen(Port, [binary, {active,false}]),
	acceptor(LSock).

acceptor(LSock) ->
	{ok, Sock} = gen_tcp:accept(LSock),
	spawn(fun() -> acceptor(LSock) end),
	login(Sock).

login(Sock) ->
	case receivePacketSize(Sock) of
		{ok, Size} ->
			case receivePacketAuth(Sock, Size) of
				{ok, Recv} ->
						Username = element(2, Recv),
						Password = element(3, Recv),
						Type = element(4, Recv),
						case Type of
							"register" -> R = client_login:create_account(Username, Password);
							"login" ->	R = client_login:login(Username, Password)
						end,
						Send_Packet = protoAuth:encode_msg(#'ResponseAuth'{
																								statusResponse=atom_to_list(R)}),

						sendPacketSize(Sock, Send_Packet),
						case atom_to_list(R) of
							"invalid" -> login(Sock);
							"user_exists"-> login(Sock);
							"ok" -> true
						end,
						reqRep(Sock);
					ok -> io:format("Socket closed~n",[])
			end;
		ok -> io:format("Socket closed~n",[])
	end.



reqRep(Sock) ->
	case receivePacketSize(Sock) of
		{ok, Size} ->
			case receivePacketGeneral(Sock, Size, 'General') of
				{ok, Recv} ->
						case Recv of
							#'General'{general={sell,#'Sell'{
																	companySell=Company,
																	qttSell=Qtt,
																	priceMin=Price,
																	clientS=ClientS,
																	host=Host,
																	port=Port}}} ->
									sell(Company, Qtt, Price, ClientS, Sock,
									Host, Port);
							#'General'{general={buy,#'Buy'{
																	companyBuy=Company,
																	qttBuy=Qtt,
																	priceMax=Price,
																	clientB=ClientB,
																	host=Host,
																	port=Port}}} ->
									buy(Company, Qtt, Price, ClientB, Sock,
									Host, Port);
							#'General'{general={logout,#'Logout'{
																	username=Username}}} ->
									logout(Username, Sock)
						end,
						reqRep(Sock);
					ok -> io:format("Socket closed~n",[])
			end;
		ok -> io:format("Socket closed~n",[])
	end.

logout(Username, Sock) ->
	R = client_login:logout(Username),
	Send2Client = protoReqRecv:encode_msg(#'ResponseAfterRecv'{rep =atom_to_list(R)}),
	sendPacketSize(Sock, Send2Client).

buy(Company, Qtt, Price, ClientB, CSock, Host, Port) ->
	%NEEDS TO GET INFO ABOUT EXCHANGE ON DIRECTORY
	case gen_tcp:connect(Host, Port, [binary,{active,false}]) of
		{ok, Sock} ->
						Send_Packet = protoReqRecv:encode_msg(#'General'{
																									general={buy,#'Buy'{
																									companyBuy=Company,
																									qttBuy=Qtt,
																									priceMax=Price,
																									clientB=ClientB}}}),
						sendPacketSize(Sock, Send_Packet),
						case receivePacketSize(Sock) of
							{ok, Size} -> case receivePacketGeneral(Sock, Size, 'ResponseAfterRecv') of
														    {ok, Recv} -> Reply = element(2, Recv),
																			Send2Client = protoReqRecv:encode_msg(#'ResponseAfterRecv'{
																														rep = Reply}),
																			sendPacketSize(CSock, Send2Client);
																ok -> io:format("Socket Closed~n",[])
														end;
							ok -> io:format("Socket Closed~n",[])
						end;
		{error, _} ->  Send2Client = protoReqRecv:encode_msg(#'ResponseAfterRecv'{
													rep = "Exchange closed, come back tomorrow"}),
									 sendPacketSize(CSock, Send2Client)
	end.

sell(Company, Qtt, Price, ClientS, CSock, Host, Port) ->
	%NEEDS TO GET INFO ABOUT EXCHANGE ON DIRECTORY
	case gen_tcp:connect(Host, Port, [binary,{active,false}]) of
			{ok, Sock} ->
							Send_Packet = protoReqRecv:encode_msg(#'General'{
																										general={sell,#'Sell'{
																										companySell=Company,
																										qttSell=Qtt,
																										priceMin=Price,
																										clientS=ClientS}}}),
							sendPacketSize(Sock, Send_Packet),
							case receivePacketSize(Sock) of
								{ok, Size} -> case receivePacketGeneral(Sock, Size, 'ResponseAfterRecv') of
															    {ok, Recv} -> Reply = element(2, Recv),
																				Send2Client = protoReqRecv:encode_msg(#'ResponseAfterRecv'{
																															rep = Reply}),
																				sendPacketSize(CSock, Send2Client);
																	ok -> io:format("Socket Closed~n",[])
															end;
								ok -> io:format("Socket Closed~n",[])
							end;
			{error, _} -> Send2Client = protoReqRecv:encode_msg(#'ResponseAfterRecv'{
														rep = "Exchange closed, come back tomorrow"}),
										sendPacketSize(CSock, Send2Client)
	end.

%%%------------ Just aux functions ---------------------%%%

sendPacketSize(Sock, Send_Packet) ->
	Tam = byte_size(Send_Packet),
	gen_tcp:send(Sock, <<Tam>>),
	gen_tcp:send(Sock, Send_Packet).

receivePacketGeneral(Sock, Size, Field) ->
	case gen_tcp:recv(Sock, Size) of
		{ok, Packet} ->
				Recv = protoReqRecv:decode_msg(Packet, Field),
				{ok, Recv};
		{error, closed} ->  gen_tcp:close(Sock)
	end.

receivePacketAuth(Sock, Size) ->
	case gen_tcp:recv(Sock, Size) of
		{ok, Packet} ->
				Recv = protoAuth:decode_msg(Packet, 'Auth'),
				{ok, Recv };
		{error, closed} -> gen_tcp:close(Sock)
	end.

receivePacketSize(Sock) ->
	case gen_tcp:recv(Sock, 1) of
		{ok, <<Tam>>} -> {ok, Tam};
		{error, closed} -> gen_tcp:close(Sock)
	end.
