@echo off
echo 五子棋服务器启动脚本
echo =====================

:: 设置CLASSPATH
set CLASSPATH=.;OOP\out\production\Gobang;OOP\lib\mysql-connector-j-8.0.33.jar

:: 检查是否已编译
if not exist OOP\out\production\Gobang\Gobang\GobangServer.class (
  echo 错误：找不到编译后的类文件，请先编译项目
  goto end
)

:: 检查MySQL连接器是否存在
if not exist OOP\lib\mysql-connector-j-8.0.33.jar (
  echo 警告：找不到MySQL连接器JAR文件
  echo 请确保已下载并放置在正确位置： OOP\lib\mysql-connector-j-8.0.33.jar
  echo 服务器将降级为内存模式运行
)

echo 启动五子棋服务器...
echo 使用类路径: %CLASSPATH%
echo.

:: 启动服务器
java -cp %CLASSPATH% Gobang.GobangServer

:end
echo.
echo 按任意键退出...
pause > nul 