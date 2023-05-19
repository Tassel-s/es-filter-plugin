# es-filter-plugin

Java的实现es-filter插件
实现了nested内部嵌套对象的遍历处理

>es版本 7.17

>使用方式：
1. mvn package
2. 解压缩elasticsearch-my-filter-plugin-1.0-SNAPSHOT.zip
3. 解压出来的my-filter整体放入es的plugin目录下
4. 重启es
5. 执行GET /_cat/plugins 确认插件是否正常工作
   



>mapping简化后结构如下 

```json
{
  "properties": {
    "address": {
      "type": "text"
    },
    "days": {
      "properties": {
        "2023-05-08": {
          "type": "nested",
          "properties": {
            "hourRoomTime": {
              "type": "keyword"
            },
            "payType": {
              "type": "long"
            },
            "price": {
              "type": "keyword"
            },
            "soldOut": {
              "type": "long"
            },
            "source": {
              "type": "keyword"
            },
            "timeLimit": {
              "type": "keyword"
            }
          }
        },
        "2023-05-09": {
          "type": "nested",
          "properties": {
            "hourRoomTime": {
              "type": "keyword"
            },
            "payType": {
              "type": "long"
            },
            "price": {
              "type": "keyword"
            },
            "soldOut": {
              "type": "long"
            },
            "source": {
              "type": "keyword"
            },
            "timeLimit": {
              "type": "keyword"
            }
          }
        }
      }
    },
    "hotelname": {
      "type": "text"
    },
    "id": {
      "type": "keyword"
    }
  }
}
```

>示例
```json
{
  "address": "东胜神州傲来国花果山水帘洞",
  "days": {
    "2023-05-06": [
      {
        "timeLimit": "4",
        "payType": 5,
        "price": "110.0",
        "hourRoomTime": [
          "06:00-23:00"
        ],
        "source": "meituan",
        "soldOut": 0
      },
      {
        "timeLimit": "4",
        "payType": 5,
        "price": "110.0",
        "hourRoomTime": [
          "06:00-23:00"
        ],
        "source": "meituan",
        "soldOut": 0
      }
    ],
    "2023-04-27": [
      {
        "payType": 3,
        "price": "65.53",
        "source": "meituan",
        "soldOut": 0
      }
    ]
  },
  "hotelname": "盘丝洞",
  "id": "10001"
}

```

使用插件搜索DSL：
```json
{
  "query": {
    "bool": {
      "must": [
        {
          //查询条件，尽量用query缩小范围
          "match_all": {}
        }
      ],
      "filter": [
        {
          "script": {
            "script": {
              "lang": "my_filter",
              "source": "pricerangelimit_v4",
              "params": {
                "maxprice": 163,
                "platprice": 400,
                "sdays": [
                  "2023-05-06"
                ],
                "minprice": 94,
                "online": [
                  "meituan",
                  "elong"
                ],
                "discountmap": {
                  "meituan": 1.00,
                  "elong": 1.00
                }
              }
            }
          }
        }
      ]
    }
  }
}

```