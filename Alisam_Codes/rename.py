import os
import re

directory = r'c:\Users\USER\OneDrive\Desktop\ABCD company\Alisam Codes\java backEnd\Alisam_Codes\src\main\resources\templates'

patterns_to_replace = [
    (re.compile(r'VJ Printers?', re.IGNORECASE), 'Alisam Cart'),
    (re.compile(r'VJ Prints', re.IGNORECASE), 'Alisam Cart'),
    (re.compile(r'ALISAM Prints', re.IGNORECASE), 'Alisam Cart'),
    (re.compile(r'Alisam Prints', re.IGNORECASE), 'Alisam Cart'),
    (re.compile(r'MM Printers', re.IGNORECASE), 'Alisam Cart'),
    (re.compile(r'VJ PRINTERS', re.IGNORECASE), 'ALISAM CART')
]

for root, dirs, files in os.walk(directory):
    for filename in files:
        if filename.endswith('.html'):
            filepath = os.path.join(root, filename)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()

            new_content = content
            for pattern, replacement in patterns_to_replace:
                new_content = pattern.sub(replacement, new_content)

            # Special cases for stylized logo where it was split up
            new_content = re.sub(r'ALISAM<span[^>]*>Prints</span>', 'Alisam Cart', new_content, flags=re.IGNORECASE)
            
            if new_content != content:
                print(f'Updated {filename}')
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)

print('Done rewriting text!')
