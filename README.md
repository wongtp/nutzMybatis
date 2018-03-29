因为在最近在用 nutzwk 的框架，但是以前用习惯了 mybatis，习惯了写 sql，再且当关联数据表多的时候，nutzwk 非常不方便，所以就瞎弄了 mybatis 进来，测试运行了了一段时间，暂时没什么问题，还把 mapper 文件热加载都弄了一下，感觉爽歪歪 b=(￣▽￣*)b

**首先看目录结构**
![这里写图片描述](https://img-blog.csdn.net/20180322221949536?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L29jcDExNA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
![这里写图片描述](https://img-blog.csdn.net/20180322221958307?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L29jcDExNA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

**运行环境**
Java 8
tomcat 9

**使用方法：**
 1. [下载地址](https://github.com/wongtp/nutzMybatis)，方便的同学点个赞呀（づ￣3￣）づ╭❤～，反正不会宕机
 2. 在项目中随便找个地方放起来，记得第二张图的两个配置文件不要放错哦~，你硬要随便放的话就修改 SqlSessionFactoryBean 就好啦~把文件路径修改一下，保证找得到就好了
 3. 看看有没有没导入的 jar 包，meven 项目记得在 pom 中加上 mybatis 哦，这里编写代码的时候都尽量不引用其他 jar 包了，所以 确实 jar 包的问题应该少点才对 
 4. 修改 nutzMybatisDB.properties 的数据库连接
 5. 这里基本都是用注解的，所以不用修改什么配置文件，只要项目能够启动找得到配置文件就行了
 6. 在其他模块新建一个 mapper文件，这里要注意的是一定要 -mapper.xml 结尾哦，不然初始化的时候找不到
 7. 然后在 controller 或者 service 中使用 @Inject 把 BaseDao 注入进来
 8. 用 BaseDao 里面的方法传入对应的  mapper 语句 id 以及参数就可以愉快地使用了
 9. 有不明白的欢迎留言哦，BaseDao 里面的的 update、delete、insert 方法很多没用过哦，基本都是用来做复杂的数据查询才使用的这个 mybatis的，所以**慎用！**^(*￣(oo)￣)^

**最后：**
这里没有打 jar 包啊，因为我发现在同时电脑中使用 自己写的热加载器（MapperReloader）会有问题 ，应该是控制不了文件监听器的线程了，暂时没有5分钟解决不了，我这边一直使用 jrebel，这两个配合起来一直没有什么为题，希望能解决的大哥哥姐姐告诉我一下哦，小弟也想多学习学习，嘻嘻 谢啦!!☆⌒(*＾-゜)v