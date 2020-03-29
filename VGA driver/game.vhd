library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_signed.all;
use ieee.std_logic_arith.all;
use ieee.std_logic_unsigned.all;
library UNISIM;
use UNISIM.Vcomponents.all;
use IEEE.NUMERIC_STD.all;



entity game is 
	port (
		board_clk, nreset : in std_logic;

		player1_left_button : in std_logic;
		player1_right_button : in std_logic;

		player2_left_button : in std_logic;
		player2_right_button : in std_logic;
		
		hsync, vsync : out std_logic;

		VGA_R  : out std_logic_vector(2 downto 0);
		VGA_G  : out std_logic_vector(2 downto 0);
		VGA_B  : out std_logic_vector(1 downto 0)
		
	);
end entity game;



architecture trinity of game is

	component vga_driver is 
		port (
		    vga_clk, vga_reset : in std_logic;

		    --Outputs to VGA port
		    h_sync  : out std_logic;
		    v_sync  : out std_logic;
		    
		    red : out std_logic_vector(2 downto 0);
		    green   : out std_logic_vector(2 downto 0);
		    blue    : out std_logic_vector(1 downto 0);
		    
		    --Outputs so other modules know where on the screen we are
		    new_frame : out std_logic;
		    current_h : out integer range 0 to 799:=0;
		    current_v : out integer range 0 to 520:=0;

		    --Inputs from other modules to set display
		    red_in   : in std_logic_vector(2 downto 0);
		    green_in  : in std_logic_vector(2 downto 0);
		    blue_in  : in std_logic_vector(1 downto 0)
	  
		);
	end component;

	component debouncer is
		port (
			deb_in : in std_logic;
			deb_clk : in std_logic;
			deb_out : out std_logic
		);
	end component;



	component color_generator is port (

	    color_clk, color_reset : in std_logic;
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

	end component;

	component FDCE
		port (
			Q : out std_logic;
			C : in std_logic;
			CE : std_logic;
			CLR : in std_logic;
			D : in std_logic
		);
	end component;

	component Clock_Divider is
        port (
        clk_in : in  std_logic;
        clk_out: out std_logic
     	);
    end component;


	--VGA signals
	signal vga_clk : std_logic:='0'; 
	signal reset : std_logic:='0'; 
	signal new_frame : std_logic:='0';

	signal set_red : std_logic_vector(2 downto 0):= (others => '0');
	signal set_green: std_logic_vector(2 downto 0):= (others => '0'); 
	signal set_blue : std_logic_vector(1 downto 0):= (others => '0');

	signal horizontal_position : integer range 0 to 799:=0;
	signal vertical_position : integer range 0 to 520:=0;

	--Score signals
	signal top_player_score : integer range 0 to 10:= 0;
	signal bottom_player_score  : integer range 0 to 10:= 0;

	signal ff1_out, not_ff1, not_slow_clock, slow_clock, divided_clock : std_logic;

	signal player1_left_stable, player1_right_stable, player2_left_stable, player2_right_stable : std_logic;

	begin 

		not_ff1 <= not ff1_out;
		not_slow_clock <= not slow_clock;

		ff1 : FDCE port map (ff1_out, board_clk, '1', '0', not_ff1);
		ff2 : FDCE port map (slow_clock, ff1_out, '1', '0', not_slow_clock);

		slower : Clock_Divider port map (board_clk, divided_clock);


		d1 : debouncer port map(player1_right_button, divided_clock, player1_right_stable);
		d2 : debouncer port map(player1_left_button, divided_clock, player1_left_stable);
		d3 : debouncer port map(player2_right_button, divided_clock, player2_right_stable);
		d4 : debouncer port map(player2_left_button, divided_clock, player2_left_stable);


		vga1 : vga_driver 
		port map (
			divided_clock,
			nreset, 
			hsync,
			vsync,
			
			VGA_R,
			VGA_G,
			VGA_B,

			new_frame,
			horizontal_position,
			vertical_position,

			set_red,
			set_green,
			set_blue
		);


		colorful : color_generator 
		port map( 

		    board_clk,
		    nreset, 
		    horizontal_position,
		    vertical_position, 

		    player1_left_stable,
		    player1_right_stable,
		    player2_left_stable,
		    player2_right_stable,

		    new_frame, 
		    set_red,
		    set_blue
	 
		);



end trinity;





