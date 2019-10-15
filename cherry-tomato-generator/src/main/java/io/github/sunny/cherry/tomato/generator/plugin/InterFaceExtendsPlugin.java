package io.github.sunny.cherry.tomato.generator.plugin;

import io.github.sunny.cherry.tomato.generator.util.StringUtils;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

/**
 * @author sunny
 * @date: 2018-06-09 21:22
 */
public class InterFaceExtendsPlugin extends PluginAdapter {
    /**
     * 根mapper
     */
    private String baseMapper;
    /**
     * 根model型
     */
    private String baseModel;


    /**
     * 主键类型，默认获取数据库表的第一个字段类型
     */
    private String primaryKeyType;

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);

        /**
         * 添加baseMapper
         */
        baseMapper = this.properties.getProperty("baseMapper");
        /**
         * 添加baseModel
         */
        baseModel = this.properties.getProperty("baseModel");
    }

    /**
     * 生成的mapper接口能够继承基类方法
     *
     * @param interfaze
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 获取实体类
        FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        // import接口
        if (!StringUtils.isEmpty(baseMapper)) {
            interfaze.addImportedType(new FullyQualifiedJavaType(baseMapper));
            interfaze.addSuperInterface(new FullyQualifiedJavaType(baseMapper + "<" + entityType.getShortName() + "," + primaryKeyType + ">"));
        }

        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine("* @author " + System.getProperty("user.name", "zhaoyunxing"));
        interfaze.addJavaDocLine("* @date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        interfaze.addJavaDocLine("*/");

        // import实体类
        interfaze.addImportedType(entityType);
        //添加 @Repository注解
        interfaze.addAnnotation("@Repository");
        interfaze.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Repository"));
        return true;
    }

    /**
     * model类方法
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        addSerialVersionUID(topLevelClass, introspectedTable);
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addAnnotation("@Data");
        //topLevelClass.addImportedType("lombok.Getter");
        //topLevelClass.addImportedType("lombok.Setter");
        //topLevelClass.addImportedType("lombok.ToString");
        //topLevelClass.addAnnotation("@Getter");
        //topLevelClass.addAnnotation("@Setter");
        //topLevelClass.addAnnotation("@ToString");


        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine("* @author " + System.getProperty("user.name", "zhaoyunxing"));
        topLevelClass.addJavaDocLine("* @date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        topLevelClass.addJavaDocLine("*/");
        // 获取表第一个字段作为主键类型
        primaryKeyType = topLevelClass.getFields().get(0).getType().getShortName();
        if (!StringUtils.isEmpty(baseModel)) {
            // topLevelClass.
            topLevelClass.addImportedType(baseModel);
            topLevelClass.addSuperInterface(new FullyQualifiedJavaType(baseModel + "<" + primaryKeyType + ">"));
        }
        return true;
    }


    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {

        return false;
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //
        topLevelClass.addFileCommentLine("/**modelPrimaryKeyClassGenerated*/");
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        /*addSerialVersionUID(topLevelClass, introspectedTable);*/
        topLevelClass.addFileCommentLine("/**modelRecordWithBLOBsClassGenerated*/");

        return true;
    }

    /**
     * set
     */
    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    /**
     * get
     */
    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    /**
     * 添加序列号id
     *
     * @param topLevelClass
     * @param introspectedTable
     */
    private void addSerialVersionUID(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        CommentGenerator commentGenerator = context.getCommentGenerator();
        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(new FullyQualifiedJavaType("long"));
        field.setStatic(true);
        field.setFinal(true);
        field.setName("serialVersionUID");
        field.setInitializationString("1L");
        commentGenerator.addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);
    }
}
