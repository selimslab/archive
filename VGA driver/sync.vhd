library ieee; use ieee.std_logic_1164.all;

entity vga_sync is
    port (
    	vga_clk, reset : in std_logic;
	    --Outputs to VGA port
		h_sync 	: out std_logic;
		v_sync 	: out std_logic;
      disp : out std_logic
    );
end entity vga_sync;

architecture arch of vga_sync is

    constant h_front_porch : integer := 16; 
    constant h_back_porch : integer := 48; 
    constant horizontal_display : integer := 640; 
    constant h_pulse_width : integer := 96;
    constant max_horizontal : integer := horizontal_display + h_front_porch + h_back_porch + h_pulse_width;


    constant v_front_porch : integer := 10; 
    constant v_back_porch : integer := 29; 
    constant v_pulse_width : integer := 2; 
    constant vertical_display : integer := 480;
    constant max_vertical : integer := vertical_display + v_front_porch + v_back_porch + v_pulse_width;




    signal h_count: integer range 0 to max_horizontal-1 := 0;
    signal v_count: integer range 0 to max_vertical-1 := 0;

    signal horizontal_pulse : std_logic := '0';
    signal vertical_pulse : std_logic := '0';

    signal h_disp: std_logic:='0';
    signal v_disp: std_logic:='0';

    begin 

    disp <= h_disp and v_disp;
    h_sync <= horizontal_pulse;
    v_sync <= vertical_pulse;


    process(vga_clk,reset)  
    begin
        if(reset = '1') then
            h_count <= 0;
        elsif rising_edge(vga_clk) then
            if(h_count = max_horizontal-1) then
                h_count <= 0;
            else
                h_count <= h_count + 1;
            end if;
        end if;
    end process;


    process(vga_clk,reset)  
    begin
        if(reset = '1') then
            horizontal_pulse <= '0';
            h_disp <= '0';
        elsif rising_edge(vga_clk) then
            if(h_count <= h_pulse_width-1) then
                horizontal_pulse <= '0';
            else
                horizontal_pulse <= '1';
                if(h_count >= h_back_porch+ h_pulse_width -1 and h_count<= max_horizontal - h_front_porch -1 ) then
                    h_disp <= '1';
                else
                    h_disp <= '0';
                end if;
            end if;
        end if;
    end process;



    process(vga_clk,reset)  
    begin
        if(reset = '1') then
            v_count <= 0;
        elsif rising_edge(vga_clk) then
            if(h_count = max_horizontal-1)then
                if(v_count = max_vertical) then
                    v_count <= 0;
                else
                    v_count <= v_count + 1;
                end if;
            end if;
        end if;
    end process;
 
    
    process(vga_clk,reset)  
    begin
        if(reset = '1') then
            vertical_pulse <= '0';
            v_disp <= '0';
        elsif rising_edge(vga_clk) then
            if(v_count <=1) then
                vertical_pulse <= '0';
            else
                vertical_pulse <= '1';
                if(v_count >= v_back_porch + v_pulse_width -1 and v_count<= max_vertical - v_front_porch -1 ) then
                    v_disp <= '1';
                else
                    v_disp <= '0';
                end if;
            end if;
        end if;
    end process;






end arch;



