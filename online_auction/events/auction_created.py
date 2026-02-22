from dataclasses import dataclass
from datetime import datetime
from ..core.base_event import BaseEvent


@dataclass
class AuctionCreated(BaseEvent):
    auction_id: str = ""
    title: str = ""
    starting_price: float = 0.0
    ends_at: datetime = None
