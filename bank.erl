
-module(bank).

-export([fun1b/2]).


fun1b(ARG1,ARG2)->
	timer:sleep(80),
	fun2(ARG1,ARG2)
	.

fun2(ARG1,ARG2)->
	receive
		{PARAM1,PARAM2}->
			PARAM3 = fun3(PARAM2,ARG2),
			if
				PARAM3 == result1->
					LOC1 = ARG2 - PARAM2,
					fun4(PARAM1, approves, ARG1, PARAM2);
				true->
					LOC1 = ARG2,
					fun4(PARAM1, denies, ARG1, PARAM2)
			end,					
			fun2(ARG1,LOC1)
	after 1000 ->
		fun5(ARG1, ARG2)
	end
	.

fun5(ARG1, ARG2)->
	ID1 = whereis(regname),
	ID1 ! {ARG1, ARG2, remaining},
	exit(whereis(ARG1), ok)
	.

fun4(PARAM1, PARAM2, PARAM3, PARAM4)->
	ID1 = whereis(PARAM1),
	ID1 ! {PARAM2},
	ID2 = whereis(regname),
	ID2 ! {PARAM3, PARAM2, PARAM4, PARAM1}
	.

fun3(PARAM1,PARAM2)->
	if
		PARAM1 =< PARAM2->
			PARAM3 = result1;
		true->
			PARAM3 = result2
	end,
	PARAM3
	.