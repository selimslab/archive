library ieee; 
use ieee.std_logic_1164.all;
use ieee.std_logic_unsigned.all;




entity color_generator is port (

	    color_clk, color_reset: in std_logic :='0';
	    hpos : in integer range 0 to 799;
	    vpos : in integer range 0 to 520;
	    is_new_frame : in std_logic;

	    player1_right : in std_logic;
	    player1_left : in std_logic;

	    player2_right : in std_logic;
	    player2_left : in std_logic;

	    red_out : out std_logic_vector (2 downto 0);
	    blue_out : out std_logic_vector (1 downto 0)
 
	);

end entity color_generator;


architecture arch of color_generator is 

	constant h_front_porch : integer := 16; 
	constant h_back_porch : integer := 48; 
	constant h_pulse_width : integer := 96;
	constant horizontal_display_width : integer := 640; 

	constant horizontal_all : integer := horizontal_display_width + h_front_porch + h_back_porch + h_pulse_width - 1 ;
	constant horizontal_start : integer := horizontal_all - horizontal_display_width;

	constant v_front_porch : integer := 10; 
	constant v_back_porch : integer := 29; 
	constant v_pulse_width : integer := 2; 
	constant vertical_display_width : integer := 480;

	constant vertical_all : integer := vertical_display_width + v_front_porch + v_back_porch + v_pulse_width - 1 ;
	constant vertical_start : integer := vertical_all - vertical_display_width;

	constant paddle_speed : integer := 5;
	constant paddle_height : integer := 15;
	constant paddle_width : integer := 80;

	constant default_ball_speed : integer := 3;
	constant ball_size : integer := 15;


	--Paddle Signals
	signal paddle_h1 : integer range horizontal_start to horizontal_all:= horizontal_start + horizontal_display_width/2;
	signal paddle_v1 : integer := vertical_start;

	signal paddle_h2 : integer range horizontal_start to horizontal_all:= horizontal_start + horizontal_display_width/2;
	signal paddle_v2 : integer := vertical_all - paddle_height -10;

	--Ball signals
	signal ball_pos_h1      : integer range horizontal_start to horizontal_all:= horizontal_start + horizontal_display_width/2;
	signal ball_pos_v1      : integer range vertical_start to vertical_all:= vertical_start + vertical_display_width/2;

	signal ball_up	  		: std_logic:= '0';
	signal ball_right		: std_logic:= '1';

	signal ball_speed_h 	: integer := default_ball_speed;
	signal ball_speed_v		: integer := default_ball_speed;

		--Score signals
	signal top_player_score : integer range 0 to 10:= 0;
	signal bottom_player_score  : integer range 0 to 10:= 0;

	signal set_red : std_logic_vector(2 downto 0);
	signal set_blue : std_logic_vector(1 downto 0);


	begin

	draw_paddle : process (color_clk) 
	begin	 
		if (rising_edge(color_clk)) then
			-- draw player 1
			if ( (vpos >= paddle_h1 and vpos <= paddle_h1 + paddle_height) and (hpos >= paddle_h1 and hpos <= paddle_h1 + paddle_width) ) then
				set_red <= "111";
			-- draw player 2
			elsif ( (hpos >= paddle_h2 and hpos <= paddle_h2 + paddle_width) and (vpos >= paddle_h2 and vpos <= paddle_h2 + paddle_height) ) then
				set_red <= "111";
			else
				set_red <= "000";
			end if;
			
		end if;		 
	end process;
	 

	draw_ball : process (color_clk) 
	begin
	 
		if (rising_edge(color_clk)) then
			if ( (hpos >= ball_pos_h1 and hpos < ball_pos_h1 + ball_size) and (vpos >= ball_pos_v1 and vpos < ball_pos_v1 + ball_size) ) then
				set_blue <= "11";
			else
				set_blue <= "00";
			end if;
			
		end if;
			 
	end process;


	move_paddle : process (color_clk, color_reset) 
	begin
	if (color_reset = '1') then 
		
		paddle_h1 <= horizontal_start + horizontal_display_width/2;
		
		paddle_h2 <= horizontal_start + horizontal_display_width/2;

	elsif (color_reset = '0') then 

	 
		if (rising_edge(color_clk) and is_new_frame = '1') then
			
			if (player2_right = '1') then  
				if (paddle_h2 < horizontal_all - paddle_width) then  
					paddle_h2 <= paddle_h2 + paddle_speed; 
				else
					paddle_h2 <= paddle_h2; 
				end if;

			elsif ( player2_left = '1') then  
				if (paddle_h2 > horizontal_start) then
					paddle_h2 <= paddle_h2 - paddle_speed;
				else
					paddle_h2 <= paddle_h2;  
				end if;
			end if;
			
			if (player1_right = '1') then
				if (paddle_h1  < horizontal_all - paddle_width) then
					paddle_h1 <= paddle_h1 + paddle_speed;
				else
					paddle_h1 <= paddle_h1;
				end if;

			elsif (player1_left ='1') then
				if (paddle_h1 > horizontal_start) then
					paddle_h1 <= paddle_h1 - paddle_speed;
				else
					paddle_h1 <= paddle_h1;
				end if;
			end if;
				
		end if;

	end if;	 

	end process;



	--Moves the ball and detects collisions 
	move_ball : process (color_clk, color_reset) 
	begin
	if (color_reset = '1') then 
		--Reset ball position
		ball_pos_v1 <= vertical_start + vertical_display_width/2;
		ball_pos_h1 <= horizontal_start + horizontal_display_width/2;

	elsif (color_reset = '0') then 

	 
		if (color_clk = '1' and is_new_frame = '1') then
		
			if (color_reset = '1') then
				top_player_score <= 0;
				bottom_player_score <= 0;
				ball_pos_v1 <= vertical_start + vertical_display_width/2;
				ball_pos_h1 <= horizontal_start + horizontal_display_width/2;
				ball_speed_h <= default_ball_speed;
				ball_speed_v <= default_ball_speed;
			else

				--If ball travelling up, and not at top
				if (ball_pos_v1  < vertical_all and ball_up = '1') then
					ball_pos_v1 <= ball_pos_v1 + ball_speed_v;
				
				--If ball travelling up and at top
				elsif (ball_up = '1') then
					ball_up <= '0';

					if (bottom_player_score  < 9) then
						bottom_player_score <= bottom_player_score + 1;
						--Reset ball position
						ball_pos_v1 <= vertical_start + vertical_display_width/2;
						ball_pos_h1 <= horizontal_start + horizontal_display_width/2;
					else
						-- game over
						ball_speed_h <= 0;
						ball_speed_v <= 0;
					end if;


				--Ball travelling down and not at bottom
				elsif (ball_pos_v1 > vertical_start and ball_up = '0') then
					ball_pos_v1 <= ball_pos_v1 - ball_speed_v;
				--Ball travelling down and at bottom
				elsif (ball_up = '0') then
					ball_up <= '1';

					if (top_player_score  < 9) then
						top_player_score <= top_player_score + 1;
						--Reset ball position
						ball_pos_v1 <= vertical_start + vertical_display_width/2;
						ball_pos_h1 <= horizontal_start + horizontal_display_width/2;
					else	
						--game over
						ball_speed_h  <= 0;
						ball_speed_v <= 0;
					end if;

				end if;
				
				--If ball travelling right, and not far right
				if (ball_pos_h1 < horizontal_all and ball_right = '1') then
					ball_pos_h1 <= ball_pos_h1 + ball_speed_h;
				--If ball travelling right and at far right
				elsif (ball_right = '1') then
					ball_right	<= '0';			
				--Ball travelling left and not at far left
				elsif (ball_pos_h1 > horizontal_start and ball_right = '0') then
					ball_pos_h1 <= ball_pos_h1 - ball_speed_h;
				--Ball travelling left and at far left
				elsif (ball_right = '0') then
					ball_right <= '1';
					
				end if;

			end if;

		-- simple collision detection 
		elsif rising_edge(color_clk) then
			--Since only the ball is blue and only the paddles are red then if they occur together a collision has happend!
			if (set_blue = "11" and set_red = "111") then
				ball_up <= ball_up XOR '1'; --Toggle horizontal ball direction on collision
			end if;
				
		end if;

	end if;

	red_out <= set_red;
	blue_out <= set_blue;
			 
	end process;



end arch;

