library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_unsigned.all;
use ieee.std_logic_arith.all;





entity vga_driver is port (
    vga_clk, vga_reset  : in std_logic;

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
 
end entity vga_driver;
 

architecture arch of vga_driver is

    constant h_front_porch : integer := 16; 
    constant h_back_porch : integer := 48; 
    constant h_pulse_width : integer := 96;
    constant horizontal_display : integer := 640; 

    constant horizontal_all : integer := horizontal_display + h_front_porch + h_back_porch + h_pulse_width - 1;
    constant horizontal_dark : integer := horizontal_all - horizontal_display;

    constant v_front_porch : integer := 10; 
    constant v_back_porch : integer := 29; 
    constant v_pulse_width : integer := 2; 
    constant vertical_display : integer := 480;

    constant vertical_all : integer := vertical_display + v_front_porch + v_back_porch + v_pulse_width - 1 ;
    constant vertical_dark : integer := vertical_all - vertical_display;

    --Store current position within screen
	signal h_pos : integer range 0 to horizontal_all :=0;  
	signal v_pos : integer range 0 to vertical_all :=0;	




    begin

    current_h <= h_pos;
    current_v <= v_pos;
 
    vga_timing : process(vga_clk, vga_reset)
        begin       
        if (vga_reset = '1') then 
            h_pos <= 0;
			v_pos <= 0;


        else
            if rising_edge(vga_clk) then
                
                --Count up pixel position
                if (h_pos < horizontal_all) then
                
                    new_frame <= '0';
                    
                    h_pos <= h_pos + 1;
                else
                    h_pos <= 0;  --Reset position at end of line
                    
                    --Count up line position
                    if (v_pos < vertical_all) then
                        v_pos <= v_pos + 1;
                    else 
                        v_pos <= 0;  --Reset position at end of frame
                        
                        new_frame <= '1';
                        
                    end if;
                end if;
                
                --Generate horizontal sync signal (negative pulse)
                if (h_pos > h_front_porch and h_pos < h_front_porch + h_pulse_width) then
                    h_sync <= '0';
                else
                    h_sync <= '1';
                end if;

                --Generate vertical sync signal (positive pulse)
                if (v_pos > v_front_porch and v_pos < v_front_porch + v_pulse_width ) then
                    v_sync <= '0';
                else
                    v_sync <= '1';
                end if;
                
                --Blank screen during FP, BP and Sync
                if ( (h_pos >= 0 and h_pos < horizontal_dark) 
					 or (v_pos >= 0 and v_pos < vertical_dark ) ) then
                        red <= "000";
                        green <= "000";
                        blue <= "00";
                        
                --In visible range of screen        
                else
                    red <= red_in;
                    green <= green_in;
                    blue <= blue_in; 
                
                end if;

            end if;

            
        end if;
            
        end process;
     



end arch;
