%% -*- coding: utf-8 -*-
%% Automatically generated, do not edit
%% Generated by gpb_compile version 4.1.1

-ifndef(protoAuthOrderErlang).
-define(protoAuthOrderErlang, true).

-define(protoAuthOrderErlang_gpb_version, "4.1.1").

-ifndef('AUTH_PB_H').
-define('AUTH_PB_H', true).
-record('Auth',
        {name = []              :: iolist(),        % = 1
         password = []          :: iolist()         % = 2
        }).
-endif.

-ifndef('ORDER_PB_H').
-define('ORDER_PB_H', true).
-record('Order',
        {company = []           :: iolist(),        % = 1
         quantity = 0           :: integer(),       % = 2, 32 bits
         price_min_max = 0      :: integer()        % = 3, 32 bits
        }).
-endif.

-endif.