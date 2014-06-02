class TablePolicyPlayer < Player
  POLICY = Hash.new do |h, k|
    h[k] = Hash.new
  end

  def self.setup(file)
    POLICY.clear

    CSV.foreach(file) do |row|
      their_hp, our_hp, stop_at = row.map(&:strip).map(&:to_i)
      POLICY[their_hp][our_hp] = stop_at
    end
  end

  setup("table_policy_self.csv")

  def strategy(victim, last_hit)
    threshold = POLICY[victim.hit_points][hit_points]
    threshold = 21 unless threshold
    if accumulated_damage >= threshold
      :attack
    else
      :roll
    end
  end
end
