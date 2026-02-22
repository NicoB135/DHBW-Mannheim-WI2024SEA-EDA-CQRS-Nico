from collections import defaultdict
from typing import Callable, Type
from .base_event import BaseEvent


class EventBus:

    def __init__(self):
        self._subscribers: dict[Type[BaseEvent], list[Callable]] = defaultdict(list)

    def subscribe(self, event_type: Type[BaseEvent], handler: Callable) -> None:
        self._subscribers[event_type].append(handler)

    def publish(self, event: BaseEvent) -> None:
        handlers = self._subscribers.get(type(event), [])
        for handler in handlers:
            handler(event)
