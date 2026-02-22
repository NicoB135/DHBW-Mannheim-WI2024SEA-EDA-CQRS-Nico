from datetime import datetime, timedelta

from online_auction.core import EventBus
from online_auction.commands import CreateAuction, PlaceBid, EndAuction
from online_auction.events import AuctionCreated, BidPlaced, BidBeaten, AuctionEnded
from online_auction.handlers import AuctionHandler, NotificationHandler, AuditLogHandler
from online_auction.queries import AuctionQueries


def setup_system() -> tuple[AuctionHandler, AuctionQueries]:
    event_bus = EventBus()
    auction_handler = AuctionHandler(event_bus)
    notification_handler = NotificationHandler()
    audit_log_handler = AuditLogHandler()
    queries = AuctionQueries(auction_handler)

    event_bus.subscribe(AuctionCreated, notification_handler.on_auction_created)
    event_bus.subscribe(BidPlaced,      notification_handler.on_bid_placed)
    event_bus.subscribe(BidBeaten,      notification_handler.on_bid_beaten)
    event_bus.subscribe(AuctionEnded,   notification_handler.on_auction_ended)

    event_bus.subscribe(AuctionCreated, audit_log_handler.on_auction_created)
    event_bus.subscribe(BidPlaced,      audit_log_handler.on_bid_placed)
    event_bus.subscribe(BidBeaten,      audit_log_handler.on_bid_beaten)
    event_bus.subscribe(AuctionEnded,   audit_log_handler.on_auction_ended)

    return auction_handler, queries


def demo_auction():
    auction_handler, queries = setup_system()

    print("=" * 55)
    print("     ONLINE-AUKTIONSSYSTEM - EDA mit CQRS")
    print("=" * 55)

    print("\n--- Auktionen erstellen ---")
    auction_handler.handle_create_auction(CreateAuction(
        auction_id="auktion-001",
        title="MacBook Pro 2023",
        starting_price=500.00,
        ends_at=datetime.now() + timedelta(days=3),
    ))
    auction_handler.handle_create_auction(CreateAuction(
        auction_id="auktion-002",
        title="PlayStation 5",
        starting_price=300.00,
        ends_at=datetime.now() + timedelta(days=1),
    ))

    print("\n--- Gebote abgeben ---")
    auction_handler.handle_place_bid(PlaceBid("auktion-001", "Alice",   520.00))
    auction_handler.handle_place_bid(PlaceBid("auktion-001", "Bob",     650.00))
    auction_handler.handle_place_bid(PlaceBid("auktion-001", "Alice",   700.00))
    auction_handler.handle_place_bid(PlaceBid("auktion-002", "Charlie", 350.00))

    print("\n\n--- QUERY: Aktueller Status ---")
    status = queries.get_current_highest_bid("auktion-001")
    print(f"  '{status['title']}': Höchstgebot {status['current_price']:.2f}€ von {status['current_bidder']}")

    print("\n--- QUERY: Alle aktiven Auktionen ---")
    for auction in queries.get_all_active_auctions():
        print(f"  [{auction['auction_id']}] {auction['title']} — {auction['current_price']:.2f}€ ({auction['current_bidder']})")

    print("\n--- QUERY: Gebotshistorie ---")
    for bid in queries.get_bid_history("auktion-001"):
        print(f"  {bid['bidder']:10} → {bid['amount']:.2f}€")

    print("\n--- Auktionen beenden ---")
    auction_handler.handle_end_auction(EndAuction("auktion-001"))
    auction_handler.handle_end_auction(EndAuction("auktion-002"))

    print("\n--- QUERY: Aktive Auktionen nach Ende ---")
    active = queries.get_all_active_auctions()
    print(f"  Noch aktive Auktionen: {len(active)}")

    print("\n" + "=" * 55)
    print("  Audit-Log wurde geschrieben: auction_audit.log")
    print("=" * 55)


if __name__ == "__main__":
    demo_auction()
