## MicroProfile Config



ConfigProvider

ConfigSource

Converters





##### 默认配置：

By default there are 3 default ConfigSources:

* _System.getProperties()_ (ordinal=400)
* _System.getenv()_ (ordinal=300)
* all _META-INF/microprofile-config.properties_ files on the ClassPath.
  (default ordinal=100, separately configurable via a config_ordinal property inside each file)

数值越大，则优先级越高！

