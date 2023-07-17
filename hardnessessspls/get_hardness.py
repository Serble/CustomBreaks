import requests
from bs4 import BeautifulSoup
import yaml

def get_blocks_from_url(url):
    response = requests.get(url)
    page = BeautifulSoup(response.text, 'html.parser')
    vitrine = page.find('div', {'id': 'vitrine'})
    entries = vitrine.find_all('div', {'class': 'entry'})

    blocks = {}
    for entry in entries:
        try:
            title_block = entry.find('h3', {'class': 'item_title'})
            if not title_block:
                continue
            id_value = title_block.get('id', '')
            if id_value.startswith('blocks:'):
                block_name = id_value.split(':')[1]
                hardness_value = entry.find('dt', string='Hardness').find_next_sibling('dd').text
                blocks[block_name] = {
                    'type': 'exact',
                    'value': block_name.upper(),
                    'tool': 'AXE',
                    'hardness': float(hardness_value)
                }
        except Exception:
            continue
    return blocks

def write_to_yaml(data, yaml_file_name):
    with open(yaml_file_name, 'w') as f:
        yaml.dump(data, f)

if __name__ == '__main__':
    url = 'https://pokechu22.github.io/Burger/1.20.1.html'
    blocks = get_blocks_from_url(url)
    write_to_yaml(blocks, 'blocks.yml')
