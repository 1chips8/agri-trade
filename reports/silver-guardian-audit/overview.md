# silver-guardian 项目摸底报告

## 基本信息

| 字段 | 值 |
| --- | --- |
| repo_path | H:\COMputerSCIence\项目\银声守护 |
| generated_at | 2026-06-04T02:54:37.734042+00:00 |
| file_count_scanned | 32 |
| approx_total_bytes | 89090 |

## 语言和文件类型

| 语言 | 文件数 |
| --- | --- |
| Other | 16 |
| Python | 16 |

## 依赖和环境线索

- `requirements.txt`

## README

- `README.md`
- `docs/README.md`
- `实施/README.txt`

## 核心链路线索

| 类别 | 命中文件数 | 代表路径 |
| --- | --- | --- |
| config | 3 | .claude/settings.local.json<br>src/config/__init__.py<br>src/config/settings.py |
| database_state | 2 | data/vector_db/.gitkeep<br>models/.gitkeep |
| evaluation | 1 | src/modules/knowledge_retrieval.py |
| inference_demo | 2 | app.py<br>src/core/app.py |
| model | 6 | models/.gitkeep<br>src/modules/__init__.py<br>src/modules/dialogue_generation.py<br>src/modules/knowledge_retrieval.py<br>src/modules/speech_recognition.py<br>src/modules/speech_synthesis.py |

## Notebook / Docker / Test 线索

### Notebooks
- 无

### Docker
- 无

### Tests
- 无

## 潜在数据/状态/模型/资源路径

- `data/.gitkeep`
- `data/chat_history/.gitkeep`
- `data/knowledge_base/knowledge_base.json`
- `data/user_data/.gitkeep`
- `data/vector_db/.gitkeep`
- `logs/.gitkeep`
- `models/.gitkeep`

## 目录树摘要

```text
银声守护/
  .claude/
  data/
  docs/
  logs/
  models/
  src/
  实施/
  .env
  .gitignore
  PROJECT_STRUCTURE.md
  README.md
  app.py
  env.example
  requirements.txt
  run.py
    settings.local.json
    chat_history/
    knowledge_base/
    user_data/
    vector_db/
    .gitkeep
      .gitkeep
      knowledge_base.json
      .gitkeep
      .gitkeep
    README.md
    .gitkeep
    .gitkeep
    config/
    core/
    modules/
    utils/
    __init__.py
      __init__.py
      settings.py
      __init__.py
      app.py
      __init__.py
      dialogue_generation.py
      knowledge_retrieval.py
      speech_recognition.py
      speech_synthesis.py
      __init__.py
      audio_utils.py
      chat_manager.py
      logger.py
    README.txt
```

## 下一步人工确认

- 找到最小可运行命令：API、页面、CLI、worker、测试、训练或 demo 至少一个。
- 确认依赖、环境变量、数据库/数据文件、端口和外部服务。
- 确认 baseline/demo 是否能在本地、Docker、云服务器或 GPU 环境上跑通。
- 确认自己要做的面试亮点：改造点、demo、测试、报告或实验计划。
