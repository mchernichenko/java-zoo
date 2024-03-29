# Apache Zookeeper и Apache Curator

## Ссылки

* [Библиотека для работы Zookeeper - Apache Curator](https://curator.apache.org/)
* [Видео на Youtube про Apache Zookeeper часть 1](https://www.youtube.com/watch?v=PgTpvzv8xp0) 
* [Ссылка на GitHub](https://github.com/dshuplyakov/zk-experiments)
* [!!! Примеры использования Apache Curator](https://programmersought.com/article/9457246297/)
* [Готовые рецерты Apache Curator (Recipes)](https://curator.apache.org/curator-recipes/index.html)
---
* [Zoo java API (baeldung)](https://www.baeldung.com/java-zookeeper)
* [Introduction to Apache Curator (baeldung)](https://www.baeldung.com/apache-curator)
* [Ещё примеры Apache Curator](https://www.programcreek.com/java-api-examples/?api=org.apache.curator.framework.CuratorFramework)
* [Ещё примеры Apache Curator](https://java.hotexamples.com/examples/org.apache.curator.framework/CuratorFrameworkFactory/builder/java-curatorframeworkfactory-builder-method-examples.html)

Apache Zookeeper - сервис координации и синхронизации для распределённых приложений.
Его можно рассматривать как key-value хранилище

Нужен когда приложение масштабируется горизонтально

Используется для следующих задач:
* выбор мастера - когда из нескольких запушенных экземпляров нужно определить главного
* Распределённая блокировка, например, есть задача по экспорту, которую должен выполнять только один экземпляр, остальные должны ждать и если чего подхватить задачу
* Список участников, например, для балансировки нагрузки
* Распределённый счётчик
* Хранение конфигураций

Zookeeper не предназначен для хранения и перекачивания большого количества данных.
Основная его задача - договориться о чём-то между инстансами и прийти к какому-то решению, например, засинхронизироваться

Zookeeper кластеризуется с выделенным мастером. Запись всегда идёт через мастер в одном потоке и транслируется на слэйвы в полуасинхроне,
т.е. половину слейвов в синхроне, на остальные слэйвы в асинхроне, так, что запись может отставать и возможна несогласованность
Команда Sync() позволяет перед чтением данных проверить их синхронизацию с мастером и получить согласованные данные. 
Чтение данных может быть произведено с любой ноды.
Итого, масштабирования на запись у zk нет, только на чтение. Чем больше сдэйвов, тем меньше скорость на запись из-за необходимости
провизинга данных на слэйвы в полусинхронном режиме, т.е. от части слэйвов мастер ждёт подтверждения.

CAP теорема - распределённая система может обладать только 2-мя из 3-х свойств:
* согласованность (Сonsistency),
* доступность (Availability)
* устойчивость к разделению (Partition Tolerance)
Zoo - это CP система, т.е. жертвуем доступностью. Если кворума нет, то zk будет недоступен.

API Zoo:
* create /path data - создать znode
* delete /path - удалить znode
* exists /path - проверить существует ли znode
* setData /path - записать данные
* getData /path - прочитать данные
* getChildren /path - получить всех потомков
* sync - синхронизация с мастером

### src\main\java\exercise\HelloWorld\ZookeeperExample.java
Пример Zookeeper Java API для создания, чтения нод и пр.

### src\main\java\exercise\HelloWorld\CuratorExample.java
Простой пример создания, чтения нод с использованием curator - более удобной обёртки к Zookeeper Java API 

# Эфемерные ноды
* Существуют пока есть активная сессия
* Не могут иметь потомков
```
curatorClient.create().withMode(CreateMode.EFEMERAL_SEQUENTAL).forPath("/app");
```
Реализуется это просто. Zookeeper клиент каждые несколько секунд шлёт Heartbeat серверу. 
Если сервер не получил запрос Heartbeat через определённое время он удаляет соединение и удаляет все эфемерные ноды связанные с клиентом,
а также рассылает остальным клиентам коллбэки

# Watcher
Представляет собой колбэк, который будет вызван, если произойдут какие-то изменения на сервере ZK.
Пример подписки на конкретный узел */cache* см. готовый рецепт от куратора **NodeCache**. Если нужно подписаться на изменения целого дерева, т.е.
ноды и всех дочерних нод, то есть готовый рецепт **TreeCache**

Watcher не потеряется, если моргнёт сеть или ZK перегрузится. Клиент куратора автоматически восстановит соединение с сервером ZK 
и восстановит отслеживание. Watcher`ы они на клиенте, сервер про них ничего не знает. 
При установлении TCP соединения клиента и сервера, при наступлении события на сервере, он отправляет
это событие в TCP соединение и клиент уже его ловит и вызывает колбэк связанный с этим событием.
По сути клиент получает все события от сервера и уже на клиенте идёт обработка этих событий и вызов колбэков.
Аналогия с RMQ, когда идёт публикация сообщений в exchange, а далее клиенты подписываются на нужные им сообщения

# Общая конфигурация
Готовый рецепт **NodeCache** или **TreeCache** (реализуется посредством Watcher`ов)

# Транзакции
Позволяют сделать группу операций атомарно, т.к. например, в конкурентрой среде делать проверку на существование ноды,
а затем её создавать может не прокатить, т.к. после проверки, кто-то её уже может создать.

# Утилитные классы Curator`a

# Тестирование
TestingServer() - поднимает zk в тестах
Требуется зависимость - 




