@echo off

set structure=com.vincentcodes.net

javadoc -cp src/ -d docs -subpackages %structure%

pause