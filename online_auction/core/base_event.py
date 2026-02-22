from dataclasses import dataclass, field
from datetime import datetime


@dataclass
class BaseEvent:
    occurred_at: datetime = field(default_factory=datetime.now)
