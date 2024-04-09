
import AppBar from '@mui/material/AppBar';
import {
    Box, Button,
    Toolbar,
    Typography
} from "@mui/material";

// @ts-expect-error I don't know what the children are
const Layout = ({children}) => {
    return (
        <>
            <Header />
            {children}

        </>
    );
}

class NavItem {
    constructor(label: string, href: string) {
        this.label = label;
        this.href = href;
    }
    label: string;
    href: string;

}

const Header = () => {
    const navItems = [
        new NavItem('Search', "search"),
        new NavItem('Home', "/"),
        new NavItem('About', "/about"),
        new NavItem('Contact', "/contact")
    ];
    return (
        <AppBar position="static">
            <Toolbar>

                <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                    BikeShed
                </Typography>
                <Box sx={{ display: { xs: 'none', sm: 'block' } }}>
                    {navItems.map((item) => (
                        <Button key={item.label} sx={{ color: '#fff' }} href={item.href} >
                                {item.label}
                        </Button>
                    ))}
                </Box>
            </Toolbar>
        </AppBar>
    )
}

// const Footer = () => {
//     return (
//         <div>
//             <p>Blah blah blah 2024</p>
//         </div>
//     )
// }

export default Layout;