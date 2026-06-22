import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
import numpy as np
import os

OUT = os.path.join(os.path.dirname(__file__), '..', 'cloud-web-frameworks', 'benchmark', 'results', 'graphs')
os.makedirs(OUT, exist_ok=True)

FRAMEWORKS = ['Spring WebFlux\n(R2DBC)', 'Spring MVC\n(JPA)', 'Spring MVC\n(Virtual Threads)']
COLORS     = ['#2196F3', '#FF5722', '#4CAF50']
SCENARIOS  = ['CRUD\n(200 VUs)', 'I/O-bound\n(500 VUs)', 'CPU-bound\n(50 VUs)', 'Saturation\n(800 VUs)']

# ── Données ────────────────────────────────────────────────────────────────────
throughput = {
    'CRUD':       [104.3, 101.9, 102.7],
    'IO':         [170.3, 253.9, 233.3],
    'CPU':        [9.9,   10.4,  15.1],
    'Saturation': [883.1, 361.2, 798.2],
}
p95 = {
    'CRUD':       [70.6,   199.1,  72.0],
    'IO':         [2323.8, 1139.6, 1692.3],
    'CPU':        [5560.5, 7062.6, 5409.9],
    'Saturation': [1711.8, 2248.2, 1041.8],
}
avg_lat = {
    'CRUD':       [23.9,   46.7,   31.9],
    'IO':         [1120.8, 683.7,  761.5],
    'CPU':        [2067.0, 1939.7, 1190.7],
    'Saturation': [329.5,  810.5,  364.1],
}

def save(fig, name):
    path = os.path.join(OUT, name)
    fig.savefig(path, dpi=150, bbox_inches='tight', facecolor='white')
    plt.close(fig)
    print(f'  -> {name}')

# ══════════════════════════════════════════════════════════════════════════════
# 1. Débit comparatif – grouped bar (tous scénarios)
# ══════════════════════════════════════════════════════════════════════════════
fig, axes = plt.subplots(1, 4, figsize=(16, 5))
fig.suptitle('Débit comparatif (req/s) – plus haut = meilleur', fontsize=13, fontweight='bold', y=1.02)

keys = ['CRUD', 'IO', 'CPU', 'Saturation']
x = np.arange(3)
for ax, key, label in zip(axes, keys, SCENARIOS):
    vals = throughput[key]
    bars = ax.bar(x, vals, color=COLORS, width=0.6, edgecolor='white', linewidth=0.5)
    for bar, v in zip(bars, vals):
        ax.text(bar.get_x() + bar.get_width()/2, bar.get_height() + max(vals)*0.01,
                f'{v:.0f}', ha='center', va='bottom', fontsize=8, fontweight='bold')
    ax.set_title(label, fontsize=10, fontweight='bold')
    ax.set_ylabel('req/s' if key == 'CRUD' else '')
    ax.set_xticks([])
    ax.set_ylim(0, max(vals) * 1.18)
    ax.spines['top'].set_visible(False)
    ax.spines['right'].set_visible(False)

patches = [mpatches.Patch(color=c, label=l.replace('\n', ' ')) for c, l in zip(COLORS, ['WebFlux (R2DBC)', 'MVC-JPA', 'MVC Virtual Threads'])]
fig.legend(handles=patches, loc='lower center', ncol=3, bbox_to_anchor=(0.5, -0.06), fontsize=9)
fig.tight_layout()
save(fig, 'throughput_comparison.png')

# ══════════════════════════════════════════════════════════════════════════════
# 2. Latence p95 – grouped bar (tous scénarios)
# ══════════════════════════════════════════════════════════════════════════════
fig, axes = plt.subplots(1, 4, figsize=(16, 5))
fig.suptitle('Latence p(95) (ms) – plus bas = meilleur', fontsize=13, fontweight='bold', y=1.02)

for ax, key, label in zip(axes, keys, SCENARIOS):
    vals = p95[key]
    bars = ax.bar(x, vals, color=COLORS, width=0.6, edgecolor='white', linewidth=0.5)
    for bar, v in zip(bars, vals):
        ax.text(bar.get_x() + bar.get_width()/2, bar.get_height() + max(vals)*0.01,
                f'{v:.0f}', ha='center', va='bottom', fontsize=8, fontweight='bold')
    ax.set_title(label, fontsize=10, fontweight='bold')
    ax.set_ylabel('ms' if key == 'CRUD' else '')
    ax.set_xticks([])
    ax.set_ylim(0, max(vals) * 1.18)
    ax.spines['top'].set_visible(False)
    ax.spines['right'].set_visible(False)

fig.legend(handles=patches, loc='lower center', ncol=3, bbox_to_anchor=(0.5, -0.06), fontsize=9)
fig.tight_layout()
save(fig, 'p95_latency_comparison.png')

# ══════════════════════════════════════════════════════════════════════════════
# 3. Saturation – zoom débit + latence côte à côte
# ══════════════════════════════════════════════════════════════════════════════
fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(10, 5))
fig.suptitle('Scénario Saturation (800 VUs)', fontsize=13, fontweight='bold')

fw_labels = ['WebFlux\n(R2DBC)', 'MVC-JPA', 'Virtual\nThreads']
sat_thr = throughput['Saturation']
sat_p95 = p95['Saturation']

bars1 = ax1.bar(x, sat_thr, color=COLORS, width=0.55, edgecolor='white')
for bar, v in zip(bars1, sat_thr):
    ax1.text(bar.get_x() + bar.get_width()/2, bar.get_height() + 10,
             f'{v:.0f}', ha='center', va='bottom', fontsize=11, fontweight='bold')
ax1.set_title('Débit (req/s)', fontsize=11)
ax1.set_xticks(x); ax1.set_xticklabels(fw_labels, fontsize=9)
ax1.set_ylabel('req/s'); ax1.set_ylim(0, max(sat_thr)*1.2)
ax1.spines['top'].set_visible(False); ax1.spines['right'].set_visible(False)

bars2 = ax2.bar(x, sat_p95, color=COLORS, width=0.55, edgecolor='white')
for bar, v in zip(bars2, sat_p95):
    ax2.text(bar.get_x() + bar.get_width()/2, bar.get_height() + 20,
             f'{v:.0f} ms', ha='center', va='bottom', fontsize=11, fontweight='bold')
ax2.set_title('Latence p(95) (ms)', fontsize=11)
ax2.set_xticks(x); ax2.set_xticklabels(fw_labels, fontsize=9)
ax2.set_ylabel('ms'); ax2.set_ylim(0, max(sat_p95)*1.2)
ax2.spines['top'].set_visible(False); ax2.spines['right'].set_visible(False)

fig.tight_layout()
save(fig, 'saturation_zoom.png')

# ══════════════════════════════════════════════════════════════════════════════
# 4. Radar / spider chart – profil global de chaque framework
# ══════════════════════════════════════════════════════════════════════════════
categories = ['CRUD\ndébit', 'I/O\ndébit', 'CPU\ndébit', 'Saturation\ndébit',
              'CRUD\np95↓', 'I/O\np95↓', 'Saturation\np95↓']
N = len(categories)

# Normalise chaque métrique 0→1 (pour débit : haut=bon ; pour latence : bas=bon → inverser)
def norm_high(vals):
    mn, mx = min(vals), max(vals)
    return [(v - mn)/(mx - mn) if mx != mn else 0.5 for v in vals]

def norm_low(vals):
    mn, mx = min(vals), max(vals)
    return [(mx - v)/(mx - mn) if mx != mn else 0.5 for v in vals]

scores = [
    norm_high([104.3, 101.9, 102.7]),   # CRUD débit
    norm_high([170.3, 253.9, 233.3]),   # IO débit
    norm_high([9.9,   10.4,  15.1]),    # CPU débit
    norm_high([883.1, 361.2, 798.2]),   # Sat débit
    norm_low([70.6,   199.1,  72.0]),   # CRUD p95
    norm_low([2323.8, 1139.6, 1692.3]), # IO p95
    norm_low([1711.8, 2248.2, 1041.8]), # Sat p95
]

# scores[i][j] = score du framework j pour la métrique i
fw_scores = [[scores[m][f] for m in range(N)] for f in range(3)]

angles = [n / float(N) * 2 * np.pi for n in range(N)]
angles += angles[:1]

fig, ax = plt.subplots(figsize=(8, 8), subplot_kw=dict(polar=True))
ax.set_theta_offset(np.pi / 2)
ax.set_theta_direction(-1)
ax.set_rlabel_position(0)
plt.xticks(angles[:-1], categories, size=9)
ax.set_ylim(0, 1)
ax.set_yticks([0.25, 0.5, 0.75, 1.0])
ax.set_yticklabels(['25%', '50%', '75%', '100%'], size=7)

fw_names = ['WebFlux (R2DBC)', 'MVC-JPA', 'MVC Virtual Threads']
for i, (fw, color) in enumerate(zip(fw_names, COLORS)):
    values = fw_scores[i] + fw_scores[i][:1]
    ax.plot(angles, values, color=color, linewidth=2, linestyle='solid', label=fw)
    ax.fill(angles, values, color=color, alpha=0.12)

ax.set_title('Profil de performance global\n(normalisé, plus grand = meilleur)',
             size=12, fontweight='bold', pad=20)
ax.legend(loc='upper right', bbox_to_anchor=(1.35, 1.15), fontsize=9)
fig.tight_layout()
save(fig, 'radar_global.png')

# ══════════════════════════════════════════════════════════════════════════════
# 5. Latence moyenne – grouped bar (tous scénarios)
# ══════════════════════════════════════════════════════════════════════════════
fig, axes = plt.subplots(1, 4, figsize=(16, 5))
fig.suptitle('Latence moyenne (ms) – plus bas = meilleur', fontsize=13, fontweight='bold', y=1.02)

for ax, key, label in zip(axes, keys, SCENARIOS):
    vals = avg_lat[key]
    bars = ax.bar(x, vals, color=COLORS, width=0.6, edgecolor='white', linewidth=0.5)
    for bar, v in zip(bars, vals):
        ax.text(bar.get_x() + bar.get_width()/2, bar.get_height() + max(vals)*0.01,
                f'{v:.0f}', ha='center', va='bottom', fontsize=8, fontweight='bold')
    ax.set_title(label, fontsize=10, fontweight='bold')
    ax.set_ylabel('ms' if key == 'CRUD' else '')
    ax.set_xticks([])
    ax.set_ylim(0, max(vals) * 1.18)
    ax.spines['top'].set_visible(False)
    ax.spines['right'].set_visible(False)

fig.legend(handles=patches, loc='lower center', ncol=3, bbox_to_anchor=(0.5, -0.06), fontsize=9)
fig.tight_layout()
save(fig, 'avg_latency_comparison.png')

print('\nTous les graphes générés.')
