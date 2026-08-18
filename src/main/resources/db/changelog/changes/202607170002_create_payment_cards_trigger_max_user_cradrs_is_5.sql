-- Counts and rejects for adding more than 5 cards
CREATE OR REPLACE FUNCTION check_user_card_limit()
RETURNS TRIGGER AS $$
DECLARE
card_count INT;
BEGIN

    PERFORM 1
    FROM payment_cards
    WHERE user_id = NEW.user_id
    FOR SHARE;

    SELECT COUNT(pc.id)
    INTO card_count
    FROM payment_cards pc
    WHERE pc.user_id = NEW.user_id;

    IF card_count >= 5 THEN
            RAISE EXCEPTION 'User with ID % has reached the maximum limit of 5 payment cards', NEW.user_id
            USING ERRCODE = 'check_violation';
    END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER enforce_max_five_cards
BEFORE INSERT OR UPDATE ON payment_cards
FOR EACH ROW
EXECUTE FUNCTION check_user_card_limit();
