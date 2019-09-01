

-module(money).


-export([start/0]).
-import(customer,[fun1c/2]).
-import(bank,[fun1b/2]).


start()->
	{RT_VAL,DATA} = file:consult("banks.txt"),
	{RT_VAL2,DATA2} = file:consult("customers.txt"),
	
	register(regname,self()),
	
	
	FUNCTION1 = fun(DATA)->
						{TMP1,TMP2} = DATA,
						timer:sleep(10),
						register(TMP1, spawn(bank, fun1b, [TMP1, TMP2])),
						TMP1
				end,
	
	FUNCTION2 = fun(DATA)->
						{TMP1,TMP2} = DATA,
						timer:sleep(10),
						TTMP = spawn(customer, fun1c, [TMP1, TMP2]),
						register(TMP1, TTMP),
						TMP1
				end,
	
	FUNCTION3 = fun(DATA)->
						{TMP1,TMP2} = DATA,
						io:format("~p: ~p~n",[TMP1,TMP2]),
						TMP1
				end,
	
	io:format("~n"),
	io:format("** Customers and loan objectives **~n"),
	TMP_DATA4 = lists:map(FUNCTION3, DATA2),
	io:format("~n"),
	io:format("** Banks and financial resources **~n"),
	TMP_DATA3 = lists:map(FUNCTION3, DATA),
	io:format("~n"),
	TMP_DATA2 = lists:map(FUNCTION2, DATA2),
	TMP_DATA = lists:map(FUNCTION1, DATA),
	mainloop(),
	io:format("~n")
	.

mainloop()->
	loop()
	.

loop()->
	receive
		{VAL1, requests, VAL2, VAL3}->
			io:format("~p requests a loan of ~p dollar(s) from ~p~n", [VAL1, VAL2, VAL3]),
			loop();
		{VAL1, approves, VAL2, VAL3}->
			io:format("~p approves a loan of ~p dollars from ~p~n", [VAL1, VAL2, VAL3]),
			loop();
		{VAL1, denies, VAL2, VAL3}->
			io:format("~p denies a loan of ~p dollars from ~p~n", [VAL1, VAL2, VAL3]),
			loop();
		{VAL1, has, VAL2}->
			io:format("~p has reached the objective of ~p dollar(s). Woo Hoo!~n", [VAL1,VAL2]),
			loop();
		{VAL1, was, VAL2}->
			io:format("~p was only able to borrow ~p dollar(s). Boo Hoo!~n", [VAL1,VAL2]),
			loop();
		{VAL1, VAL2, remaining}->
			io:format("~p has ~p dollar(s) remaining.~n",[VAL1, VAL2]),
			loop()
	after 1000 ->
		exit(whereis(regname), ok)
	end
	.