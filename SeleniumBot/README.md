# Selenium Bot

-[ ] 自动化测试核心模块

## Q: 如果此模块存在 入口启动类，那么启动 WebBot 就会出现 service 包的 Bean 重复创建的问题？这是为什么？

R1: 和 启动类的自身的自动扫描装配的功能有关，他会扫描同包底下所有的类扫描并将其装配。
同时，由于 BotWeb 启动类配置了自动扫描装配的功能，也会为其此模块的所有类扫描装配，所以导致同时存在两个相同的 Bean.