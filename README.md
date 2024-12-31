dview-flow-layout
![Release](https://jitpack.io/v/dora4/dview-flow-layout.svg)
--------------------------------

#### 卡片

![Dora视图_Flow_Layout](https://github.com/user-attachments/assets/2cb86a48-43fe-4d38-a0d0-6e5204c4166b)
![Dora视图_容器喷射者-](https://github.com/user-attachments/assets/17ee5b8c-73b6-4c90-8a3b-fc935890b939)
##### 卡名：Dora视图 Button 
###### 卡片类型：效果怪兽
###### 属性：水
###### 星级：4
###### 种族：水族
###### 攻击力/防御力：500/1500
###### 效果：此卡不会因为对方卡的效果而破坏，并可使其无效化。此卡攻击里侧守备表示的怪兽时，若攻击力高于其守备力，则给予对方此卡原攻击力的伤害，并抽一张卡。一回合一次，可额外抽一张卡。

#### Gradle依赖配置

```groovy
// 添加以下代码到项目根目录下的build.gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
// 添加以下代码到app模块的build.gradle
dependencies {
    implementation 'com.github.dora4:dview-flow-layout:1.0'
}
```
