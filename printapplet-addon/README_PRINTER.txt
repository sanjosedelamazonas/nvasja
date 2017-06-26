Configuration of the Dot Matrix printer

The printer can be attached directly using the parallel port (LPT1) or 
it may be installed on the network or even the localhost using USB or any other 
connection. In that case the parallel port should be mapped to the network attached
printer using the following command:
NET USE LPT1  \\host\printer /PERSISTENT:YES

   