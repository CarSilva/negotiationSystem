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
	Size = receivePacketSize(Sock),
	case receivePacketAuth(Sock, Size) of
		{ok, Recv} ->
				Username = element(2, Recv),
				Password = element(3, Recv),
				Type = element(4, Recv),
				case Type of
					"registo" -> R = client_login:create_account(Username, Password);
					"login" ->	R = client_login:login(Username, Password)
				end,
				Send_Packet = protoAuth:encode_msg(#'ResponseAuth'{statusResponse=atom_to_list(R)}),
				sendPacketSize(Sock, Send_Packet),
				reqRep(Sock);
		{error, Reason} -> Reason
	end.


reqRep(Sock) ->
	Size = receivePacketSize(Sock),
	case receivePacketGeneral(Sock, Size, 'General') of
		{ok, Recv} ->
				case Recv of
						#'General'{general={buy,#'Buy'{companyBuy=Company,qttBuy=Qtt,priceMax=Price}}} ->
								Send_Packet = protoReqRecv:encode_msg(#'Reply'{reply={rAR,#'ResponseAfterRecv'{rep = "Success"}}}),
								sendPacketSize(Sock, Send_Packet),
								buy(Company, Qtt, Price);
						#'General'{general={sell,#'Sell'{companySell=Company,qttSell=Qtt,priceMin=Price}}} ->
								Send_Packet = protoReqRecv:encode_msg(#'Reply'{reply={rAR,#'ResponseAfterRecv'{rep = "Success"}}}),
								sendPacketSize(Sock, Send_Packet),
								sell(Company, Qtt, Price)
				end,
				reqRep(Sock);
		{error, Reason} -> io:format("Some error to be fix")
	end.

buy(Company, Qtt, Price) -> true.
sell(Company, Qtt, Price) -> true.

sendPacketSize(Sock, Send_Packet) ->
	Tam = byte_size(Send_Packet),
	gen_tcp:send(Sock, <<Tam>>),
	gen_tcp:send(Sock, Send_Packet).

receivePacketGeneral(Sock, Size, Field) ->
	case gen_tcp:recv(Sock, Size) of
		{ok, Packet} ->
				Recv = protoReqRecv:decode_msg(Packet, Field),
				{ok, Recv };
		{error, Reason} -> {error, Reason}
end.

receivePacketAuth(Sock, Size) ->
	case gen_tcp:recv(Sock, Size) of
		{ok, Packet} ->
				Recv = protoAuth:decode_msg(Packet, 'Auth'),
				{ok, Recv };
		{error, Reason} -> {error, Reason}
	end.

receivePacketSize(Sock) ->
	case gen_tcp:recv(Sock, 1) of
		{ok, <<Tam>>} -> Tam;
		{error, Reason}  -> Reason
	end.
