使用原子变量
====

原子变量在 Java 5 中被引入以在单个变量上提供原子操作。
对于普通变量，在编译程序时，用Java实现的每个操作被转化为数条机器能理解的指令。
例如赋值给一个变量，在Java中只用一条语句，但编译此程序时，这条语句被转化为JVM语言的多条指令。
此事实在多个线程共享一个变量时能造成数据不一致的错误。

为了避免此问题，Java 引入了原子变量。当一个线程操作一个原子变量时，如果其他线程想操作同一个变量，
原子变量类的实现包含一个检查操作是否一步完成的机制。基本上是：

1. 获取此变量的值
2. 在一个本地变量中改变它的值
3. 尝试以新值替换旧值。
    * 如果旧值没被其他操作改变，仍和原来一样，则替换它
    * 如果已被其他操作改变，和原来不一样了，则重新从第1步开始

此操作被称为 比较并设置。

原子变量不使用锁或其他同步机制来保护对其值的访问。所有操作都基于比较并设置操作。
能保证几个线程同时操作一个原子变量也不会产生数据不一致的问题，并且性能比用同步机制保护的普通变量要好。


### 任务

使用原子变量实现一个银行账号和2个不同的任务。一个向账号加钱，一个从账号减钱。
例子中将使用 AtomicLong 类。



### 实现

*本节的示例代码在 com.elanzone.books.noteeg.chpt6.sect08 package中*


* 数据类 : Account

    * 属性 balance : AtomicLong 对象，账户余额
    * 构造函数初始化 balance 为一个新 AtomicLong 对象

            private AtomicLong balance;

            public Account() {
                balance = new AtomicLong();
            }

    * getBalance() 方法：使用 get() 方法获取 AtomicLong 变量的值

            public long getBalance() {
                return balance.get();
            }

    * setBalance() 方法: 使用 set() 方法给 AtomicLong 变量赋值

            public void setBalance(long amount) {
                balance.set(amount);
            }

    * addAmount() 方法: 使用 getAndAdd() 方法

            public void addAmount(long amount) {
                balance.getAndAdd(amount);
            }

    * subtractAmount() 方法:

            public void subtractAmount(long amount) {
                this.balance.getAndAdd(-amount);
            }

* Runnable 实现类 : Company

    * 属性 account : Account 对象，账号
    * 在构造函数将属性初始化为参数值

            private Account account;

            public Company(Account account) {
                this.account = account;
            }

    * run() 方法：循环10次，每次调用 addAmount() 方法向账号加 1000

            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    account.addAmount(1000);
                }
            }

* Runnable 实现类 : Bank

    * 属性 account : Account 对象，账号
    * 在构造函数将属性初始化为参数值

            private Account account;

            public Bank(Account account) {
                this.account = account;
            }

    * run() 方法: 循环10次，每次调用 subtractAmount() 方法向账号减 1000

            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    account.subtractAmount(1000);
                }
            }

* 控制类 : Main

    1. 创建一个 Account 对象，设置账户余额初始值为 1000
    2. 创建一个 Company 对象及对应的线程
    3. 创建一个 Bank 对象及对应的线程
    4. 启动2个线程并等待线程的结束，在此前后输出账户余额

                Account account = new Account();
                account.setBalance(1000);

                Company company = new Company(account);
                Thread companyThread = new Thread(company);

                Bank bank = new Bank(account);
                Thread bankThread = new Thread(bank);

                System.out.printf("Account : Initial Balance: %d\n", account.getBalance());

                companyThread.start();
                bankThread.start();

                try {
                    companyThread.join();
                    bankThread.join();
                    System.out.printf("Account : Final Balance: %d\n", account.getBalance());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



### 讲解

本例的关键在于 Account 类。在此类中，声明了一个名为 balance 的 AtomicLong 变量以保存账户余额。
然后实现了使用 AtomicLong 类提供的方法来操作余额的方法。

* 为了实现返回 balance 属性值的 getBalance() 方法，使用了 AtomicLong 类的 get() 方法
* 为了实现设置 balance 属性值的 setBalance() 方法，使用了 AtomicLong 类的 set() 方法
* 为了实现增加账户余额的 addAmount() 方法，使用了 AtomicLong 类的 getAndAdd() 方法
* 为了实现减少 balance 属性值的 subtractAmount() 方法，也使用了 AtomicLong 类的 getAndAdd() 方法

接着，实现了2个不同的任务:

* Company 类模拟一个公司增加账户的余额。此类的每个任务给账号加10次1000
* Bank 类模拟一个银行，账户所有者在银行里取钱。此类的每个任务给账户减10次1000

在 Main 类中，创建一个余额有1000的Account对象。然后执行一个 bank 任务和一个 company 任务。
所以账户的最终余额必须和初始余额一致。


### 了解更多

在 Java 中还有一些其他的原子类。例如 AtomicBoolean、AtomicInteger 和 AtomicReference。