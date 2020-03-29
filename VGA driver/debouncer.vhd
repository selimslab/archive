library ieee; 
use ieee.std_logic_1164.all;

entity debouncer is
	port (
		deb_in : in std_logic;
		deb_clk : in std_logic;
		deb_out : out std_logic
	);
end debouncer;

architecture deb of debouncer is
begin

	process (deb_clk)
		variable a, b, c, d : std_logic;
	begin
		if (deb_clk'event and deb_clk = '1') then
			d := c;
			c := b;
			b := a;
			a := deb_in;
		end if;

		deb_out <= (a and b) and (c and d);

	end process;

end deb;