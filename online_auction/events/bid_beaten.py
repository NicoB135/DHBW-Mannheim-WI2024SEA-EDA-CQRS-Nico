from dataclasses import dataclass
from ..core.base_event import BaseEvent


@dataclass
class BidBeaten(BaseEvent):
    auction_id: str = ""
    previous_bidder: str = ""
    previous_amount: float = 0.0
    new_bidder: str = ""
    new_amount: float = 0.0
