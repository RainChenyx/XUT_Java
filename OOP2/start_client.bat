@echo off
echo 五子棋客户端启动脚本
echo =====================

:: 设置CLASSPATH
set CLASSPATH=.;OOP\out\production\Gobang;OOP\lib\mysql-connector-j-8.0.33.jar

:: 检查是否已编译
if not exist OOP\out\production\Gobang\Gobang\GobangClient.class (
  echo 错误：找不到编译后的类文件，请先编译项目
  goto end
)

echo 启动五子棋客户端...
echo 使用类路径: %CLASSPATH%
echo.

:: 启动客户端
java -cp %CLASSPATH% Gobang.GobangClient

:end
echo.
echo 按任意键退出...
pause > nul 