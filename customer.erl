-module(customer).


-export([fun1c/2]).

fun1c(ARG1,ARG2)->
	timer:sleep(100),
	{RT_VAL,DATA} = file:consult("banks.txt"),
	FUNCTION1 = fun(DATA)->
						{TMP1,TMP2} = DATA,
						TMP1
				end,
	
	TMP_DATA = lists:map(FUNCTION1, DATA),
	fun2(ARG1,ARG2,TMP_DATA,ARG2)
	.

fun2(ARG1,ARG2,ARG3,ARG4)->
	timer:sleep(crypto:rand_uniform(10, 100)),
	LOC1 = random:uniform(50),
	LOC2 = fun3(ARG2,LOC1),
	if
		LOC2 > 0->
			LOC3 = fun4(ARG3),
			if LOC3 == -1->
				   fun6(ARG1,ARG2,ARG4);
			   true->
					fun5(ARG1, LOC2, LOC3),
					fun8(ARG1, ARG2, ARG3, ARG4, LOC2, LOC3)
			end;
			
		true->
			fun7(ARG1,ARG4)
	end
	.

fun8(ARG1, ARG2, ARG3, ARG4, ARG5, ARG6)->
	receive
		{approves}->
			LOC1 = ARG2 - ARG5,
			fun2(ARG1, LOC1, ARG3, ARG4);
		{denies}->
			fun2(ARG1, ARG2, lists:delete(ARG6, ARG3), ARG4)
	after 1000 ->
		exit(whereis(ARG1), ok)
	end
	.

fun7(ARG1,ARG2)->
	ID1 = whereis(regname),
	ID1 ! {ARG1,has,ARG2}
	.

fun6(ARG1,ARG2,ARG3)->
	LOC1 = ARG3 - ARG2,
	ID1 = whereis(regname),
	ID1 ! {ARG1,was,LOC1}
	.

fun5(ARG1, ARG2, ARG3)->
	ID1 = whereis(ARG3),
	ID1 ! {ARG1, ARG2},
	ID2 = whereis(regname),
	ID2 ! {ARG1, requests, ARG2, ARG3}
	.

fun4(ARG1)->
	if
		length(ARG1) > 0->
			LOC1 = random:uniform(length(ARG1)),
			LOC2 = lists:nth(LOC1, ARG1);
		true->
			LOC2 = -1
	end,
	LOC2
	.

fun3(ARG1,ARG2)->
	if
		ARG1 =< ARG2->
			LOC1 = ARG1;
		true->
			LOC1 = ARG2
	end,
	LOC1
	.