CREATE OR REPLACE VIEW posts_with_meta AS
SELECT
p.id as id,
p.timestamp as timestamp,
p.adresa as adresa,
p.ime as ime,
p.vlasnik as vlasnik,
a.profile->>'avatar' as avatar,
count(b.user_id) as boosts
from oglasi as p
left join users as a on a.login = p.vlasnik
left join boosts as b on p.id = b.post_id
group by p.id, a.login
--;;
CREATE OR REPLACE VIEW posts_and_boosts AS
select
p.id as id,
p.timestamp as timestamp,
p.adresa as adresa,
p.ime as ime,
p.vlasnik as vlasnik,
p.avatar as avatar,
p.boosts as boosts,
b.post_id is not null as is_boost,
coalesce(b.timestamp, p.timestamp) as posted_at,
coalesce(b.user_id, p.vlasnik) as poster,
coalesce(u.profile->>'avatar', p.avatar) as poster_avatar,
coalesce(b.poster, p.vlasnik) as source,
coalesce(s.profile->>'avatar', p.avatar) as source_avatar
from posts_with_meta as p
left join boosts as b on b.post_id = p.id
left join users as u on b.user_id = u.login
left join users as s on b.poster = s.login