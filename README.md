# NettyRPCSprinBoot Project

基于Netty.io的一个高性能JAVA RPC框架，可选使用kryo,hessian,protostuff来进行序列化操作。

### 前言

​		本项目为某天网上冲浪闲逛时发现一篇博文[谈谈如何使用Netty开发实现高性能的RPC服务器 - Newland - 博客园 (cnblogs.com)](https://www.cnblogs.com/jietang/p/5615681.html) ，观看之后感觉作者写得十分不错，把Netty 与Spring框架很好得结合了起来，配合Spring的各类特性，达到了一个几乎可用于生产环境的RPC框架，查看原项目[NettyRPC](https://github.com/tang-jie/NettyRPC) 发现此项目已于2018年年初停止维护更新，也难以找到作者的痕迹，抱着学习的心理，本人clone了此项目，并且基于将原基于Spring的项目进行了升级改造。就有了现在的SpringBoot，期望真正能够做到开箱即用，及高拓展性，高性能，对于原项目进行解耦，更加易于阅读学习，满足SpringBoot的开发规约，对项目的Bean注入，服务注册等功能进行优化。

​		基于原项目的二次开发遵循开源协议，除包名更改，及新开发组件功能变更之外，其他类注释均保留原作者信息。这里也只是对项目的二次开发，并无剽窃原作果实之意(希望开源越来越好: )。

​        原项目地址：[NettyRPC](https://github.com/tang-jie/NettyRPC)

​		原项目开发指南：[开发指南](https://github.com/tang-jie/NettyRPC/wiki/NettyRPC%E5%BC%80%E5%8F%91%E6%8C%87%E5%8D%97)

### 其他





## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)



