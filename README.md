## BeBetter -- 备忘录app实验报告

### 功能介绍

#### 首页

- 备忘录首页展示的是用户当前的所有备忘，每一条备忘有自己的状态（未完成，已过期，已完成），同时展示备忘的信息和时间

- 同时可以通过点击最上方的下拉框，根据提前设置的分类将备忘过滤，更好地处理不同类型的备忘

- 也可以通过点击图标将不同的状态的备忘收起

- 通过点击备忘进入备忘的编辑页面

- 点击右下方的加号按钮，同样会进行备忘的添加

- 在下拉的分类框中，以及注意的最顶部，均会统计当前备忘的数量

  <img src="C:\Users\14303\AppData\Roaming\Typora\typora-user-images\image-20230216131812099.png" alt="image-20230216131812099" style="zoom: 25%;" /><img src="C:\Users\14303\AppData\Roaming\Typora\typora-user-images\image-20230216131836716.png" alt="image-20230216131836716" style="zoom:25%;" />

#### 备忘的编辑页

- 通过点击备忘或添加按钮，来对备忘进行更新删除，或者添加等

- 备忘主要有内容和开始时间，截止时间，备注，以及分类构成，内容必填，（开始时间期望填写，若填写开始时间，则设置闹钟服务，并且会在预设的时间发出通知），其他的可以不设置，分类不选择会默认设置为未分类

- 通过点击返回按钮放回主页，通过点击保存按钮进行更新或添加备忘，然后会回到主页，并且刷新数据

- 点击分类的文本会打开下拉框，进行选择或者新建分类

  <img src="C:\Users\14303\AppData\Roaming\Typora\typora-user-images\image-20230216133805988.png" alt="image-20230216133805988" style="zoom:25%;" /><img src="C:\Users\14303\AppData\Roaming\Typora\typora-user-images\image-20230216133819812.png" alt="image-20230216133819812" style="zoom:25%;" />

#### 备忘分类添加

- app可以通过三处进行备忘分类的创建，分别是主页分类下拉框的新建按钮，备忘编辑页分类的下拉按钮，以及备忘分类的遍及页的新建按钮，都可以触发新建分类的弹窗

- 分类框会有八种颜色的选择框，进行颜色的选择，同时创建分类的名称

- 新建分类时可以选择预设的八种颜色，作为图标的颜色，同时会有八种浅色与之对应，作为主页的背景色

  <img src="C:\Users\14303\AppData\Roaming\Typora\typora-user-images\image-20230216134945267.png" alt="image-20230216134945267" style="zoom:25%;" /><img src="C:\Users\14303\AppData\Roaming\Typora\typora-user-images\image-20230216134956466.png" alt="image-20230216134956466" style="zoom:25%;" />

  

#### 备忘分类的编辑页

- 编辑页通过主页分类下拉框的编辑按钮进入，该页面主要查看当前的分类，以及对分类进行更新删除，以及添加
- 通过点击分类弹出弹窗，在其进行名称和颜色的修改
- 点击删除的图标来对分类进行删除

<img src="C:\Users\14303\AppData\Roaming\Typora\typora-user-images\image-20230216135600778.png" alt="image-20230216135600778" style="zoom:25%;" /><img src="C:\Users\14303\AppData\Roaming\Typora\typora-user-images\image-20230216135612449.png" alt="image-20230216135612449" style="zoom:25%;" />

#### 备忘提醒

- app会进行判断，将设置了开始时间的备忘加入系统闹钟服务，在对应的时间发送广播，由设置的recevier进行接收，并且发送通知，显示在通知栏以及出现悬浮窗进行提醒

- 通过点击通知会重新回到主页，查看当前的备忘

- 同时通知会显示当前备忘的内容

  <img src="C:\Users\14303\AppData\Roaming\Typora\typora-user-images\image-20230216140712044.png" alt="image-20230216140712044" style="zoom:25%;" /><img src="C:\Users\14303\AppData\Roaming\Typora\typora-user-images\image-20230216140759438.png" alt="image-20230216140759438" style="zoom:25%;" />

#### 备忘完成的位置统计

- 当用户点击完成备忘的按钮时，app会记录用户完成备忘的位置，并且会在足迹的页面（即地图页面标注出来）
- 当用户单击地图上的marker时，会在下方显示该备忘完成的时间，内容，以及详细的位置，为用户显示其主要完成备忘的地点

<img src="C:\Users\14303\AppData\Roaming\Typora\typora-user-images\image-20230216142150565.png" alt="image-20230216142150565" style="zoom:25%;" />

#### 个人设置页

- 更换头像，这里可以通过系统相册进行选择图片作为头像，同时可以选择拍照，打开照相机拍照，并且将返回的图片流转存到手机本地进行存储。
- 备份数据，可以通过点击备份数据的选项，可以将手机的本地数据上传到数据库，将用户的账号（注册时由用户定义）作为识别的id存入数据库中,在此将本人电脑作为云端数据库。
- 下载数据，根据本地的账号获取数据库中的数据，并将其存入至本地sqlite数据库中
- 退出登录，用户退出登录。

<img src="C:\Users\14303\AppData\Roaming\Typora\typora-user-images\image-20230216202136834.png" alt="image-20230216202136834" style="zoom:25%;" /><img src="C:\Users\14303\AppData\Roaming\Typora\typora-user-images\image-20230216202147801.png" alt="image-20230216202147801" style="zoom:25%;" />

#### 数据展示页

- 利用饼状图来显示用户完成备忘的情况，显示用户完成各个分类的情况，显示用户的倾向
- 利用折线图显示前七天用户完成备忘的数量

<img src="C:\Users\14303\AppData\Roaming\Typora\typora-user-images\image-20230216203433270.png" alt="image-20230216203433270" style="zoom:25%;" />

#### 登录注册页

- app会检查之前是否登录过，如果没有就会跳转至登录页，进行账号密码登录
- 同时可以在登录页点击注册，输入账号，密码，用户名后，点击注册即可

<img src="C:\Users\14303\AppData\Roaming\Typora\typora-user-images\image-20230216203517653.png" alt="image-20230216203517653" style="zoom:25%;" /><img src="C:\Users\14303\AppData\Roaming\Typora\typora-user-images\image-20230216203527799.png" alt="image-20230216203527799" style="zoom:25%;" />

#### 小组件（widget）

- 左侧显示日期，星期，以及添加按钮，在点击添加按钮之后便会进入备忘的添加页
- 右侧显示所有的待办备忘，上方显示备忘的数量，下方显示各个备忘的详细情况，点击后会进入备忘的编辑页
- 点击其他地方可以进入app主页
- <img src="C:\Users\14303\AppData\Roaming\Typora\typora-user-images\image-20230216201919482.png" alt="image-20230216201919482" style="zoom:25%;" />



### 收获和感想

通过实现这个app,我比较系统地了解了一个安卓应用程序的开发流程，对安卓的各个组件有了比较清晰的认识，同时因为某些需求，需要重写底层的逻辑，对安卓开发的技巧得到了锻炼和提升。例如，在实现widget的时候，比较系统的了解的小组件的开发过程，通过这个app,让我成功的构建出一个完整的app,这为我以后继续开发安卓程序有了坚实的基础。
