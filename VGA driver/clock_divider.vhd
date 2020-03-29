
----------------------------------------------------------------------------------
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;


	entity Clock_Divider is
		Port (
		clk_in : in  std_logic;
		clk_out: out std_logic);
	end Clock_Divider;
	 
	architecture Behavioral of Clock_Divider is
		signal tempclk: std_logic:='0';
		signal counter : integer range 0 to 1 := 0;
	begin
		clock_divider: process (clk_in) begin
			if rising_edge(clk_in) then
				if (counter = 1) then
					tempclk <= NOT tempclk;
					counter <= 0;
				else
					counter <= counter + 1;
				end if;
			end if;
		end process;
		
		clk_out <= tempclk;
	
	
	end Behavioral;
