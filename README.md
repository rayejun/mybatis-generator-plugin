# mybatis-generator-plugin
基于mybatis-generator-core 1.4.0版本增强插件
1. 添加实体类生成数据库表字段注释实现io.github.rayejun.mybatis.generator.internal.SimpleCommentGenerator
2. 添加io.github.rayejun.mybatis.generator.plugin.MethodPlusPlugin插件,生成insertOrUpdate、insertOrUpdateSelective、batchInsert、updateBatch、updateBatchSelective方法
