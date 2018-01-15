-module(main).
-export([server/1]).
-include("protobuf/protoAuthErlang.hrl").
-include("protobuf/protoReqRecvErlang.hrl").

server(Port) ->
	client_login:start(),
	{ok, LSock} = gen_tcp:listen(Port, [binary, {active,false}]),
	acceptor(LSock).

acceptor(LSock) ->
	{ok, Sock} = gen_tcp:accept(LSock),
  	spawn(fun() -> acceptor(LSock) end),
		login(Sock).

login(Sock) ->
	Size = receivePacketSize(Sock),
	case receivePacket(Sock, Size, 'Auth') of
		{ok, Recv} ->
				Username = element(2, Recv),
				Password = element(3, Recv),
				Type = element(4, Recv),
				case Type of
					"registo" -> R = client_login:create_account(Username, Password);
					"login" ->	R = client_login:login(Username, Password)
				end,
				Send_Packet = protoAuthErlang:encode_msg(#'ResponseAuth'{statusResponse=atom_to_list(R)}),
				sendPacketSize(Sock, Send_Packet),
				reqRep(Sock);
		{error, Reason} -> Reason
	end.


reqRep(Sock) ->
	Size = receivePacketSize(Sock),
	case receivePacket(Sock, Size, 'General') of
		{ok, Recv} ->
				case Recv of
						#'General'{general = {order,#'Order'{company=Company, quantity=Qtt, price_min_max=Price}}} ->
								order(Company, Qtt, Price)
				end;
		{error, Reason} -> Reason
	end.

order(Company, Qtt, Price) ->
		Price.

sendPacketSize(Sock, Send_Packet) ->
	Tam = byte_size(Send_Packet),
	gen_tcp:send(Sock, <<Tam>>),
	gen_tcp:send(Sock, Send_Packet).


receivePacket(Sock, Size, Type) ->
	case gen_tcp:recv(Sock, Size) of
		{ok, Packet} ->
				Recv = protoAuthErlang:decode_msg(Packet, Type),
				{ok, Recv };
		{error, Reason} -> {error, Reason}
	end.

receivePacketSize(Sock) ->
	case gen_tcp:recv(Sock, 1) of
		{ok, <<Tam>>} -> Tam;
		{error, Reason}  -> Reason
	end.
