from dataclasses import dataclass
from ..core.base_event import BaseEvent


@dataclass
class AuctionEnded(BaseEvent):
    auction_id: str = ""
    title: str = ""
    winner_name: str = ""
    winning_bid: float = 0.0
