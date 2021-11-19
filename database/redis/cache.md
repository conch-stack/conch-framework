## Cache



### 客户端缓存

##### 页面缓存

- 含义：
  - 页面自身对某些元素或全部元素进行缓存
  - 服务端将静态页面或者动态页面进行缓存，然后给客户端使用 - CDN
- 比如：HTML5目前就支持页面的离线缓存和本地存储

##### 浏览器缓存

有http 头决定缓存策略，浏览器会将资源缓存到本地

##### App缓存

- Android：
  - 轻量级缓存框架：ASampleCache
  - 可以缓存：Json、BitMap、序列化的Java对象和二进制数据等
- IOS：
  - 常用缓存框架：SDWebImage、NsCache等



### 缓存更新策略

##### Cache Aside

> 先读缓存，如果缓存没有，就读数据库，再将结果写入缓存
>
> 删除或更新时，同时删除缓存

##### Read/Write Through

##### Write Behind