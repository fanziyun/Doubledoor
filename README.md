# Doubledoor

一个轻量级 Fabric 双开门模组，专为木门设计。

## 功能特性

- **自动同步**: 点击一扇木门时，相邻且方向合法的木门会自动同步开启或关闭。
- **材质兼容性**: 支持配置是否允许不同材质的木门（如橡木门与深色橡木门）组成双开门。
- **红石支持**: 可选开启红石信号触发双开逻辑。
- **潜行保护**: 支持配置是否在潜行时禁用双开逻辑，方便精准操作。

## 配置文件 (config/doubledoor.json)

```json
{
  "allowDifferentMaterials": true,
  "enableRedstone": false,
  "allowSneaking": false
}
```

## 技术规格
　　　
- **支持版本**: Minecraft 1.21.x (Fabric)
- **依赖环境**: Java 21+, Fabric API
- **映射表**: Mojang 官方映射 (Mojmap)

## 多版本兼容

项目结构支持多版本分支。如需支持其他版本：
1. 在 `fabric.mod.json` 中修改 `minecraft` 依赖版本
2. 根据需要调整 `gradle.properties` 中的版本号
